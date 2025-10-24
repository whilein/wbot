/*
 *    Copyright 2024 Whilein
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package wbot.platform.vk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.jackson.Jacksonized;
import lombok.val;
import org.slf4j.Logger;
import wbot.http.HttpClient;
import wbot.platform.AbstractLongPoll;
import wbot.platform.vk.model.update.Update;
import wbot.platform.vk.model.update.UpdateObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class VkLongPoll extends AbstractLongPoll<UpdateObject> {
    private static final String URL = "%s?act=a_check&key=%s&ts=%s&wait=90";

    private static final int TIMEOUT_SECONDS = 300000; // 5 минут

    VkClient vkClient;

    long groupId;

    HttpClient httpClient;
    JsonMapper jsonMapper;

    @NonFinal
    String uri;

    @NonFinal
    String server, key, ts;

    public VkLongPoll(Logger logger, VkClient vkClient, long groupId) {
        super(logger);

        this.vkClient = vkClient;
        this.groupId = groupId;

        this.httpClient = vkClient.getHttpClient();
        this.jsonMapper = vkClient.getJsonMapper();
    }

    @Override
    protected void onStart() throws Exception {
        try {
            updateServer();
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    private void updateServer() throws ExecutionException, InterruptedException {
        val result = vkClient.groupsGetLongPollServer()
                .groupId(groupId)
                .make()
                .get();

        server = result.getServer();
        key = result.getKey();
        ts = result.getTs();

        updateUri();

        logger.info("LongPoll server changed: " + server);
    }

    private void updateUri() {
        this.uri = String.format(URL,
                server,
                key,
                ts);
    }

    @Value
    @Builder
    @Jacksonized
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Response {
        int failed;
        Object ts;
        List<Update> updates;
    }

    @Override
    protected void poll(Consumer<UpdateObject> updateHandler) throws Exception {
        val response = httpClient.get(uri)
                .thenApply(value -> {
                    try (val content = value.getContent()) {
                        return jsonMapper.readValue(content, Response.class);
                    } catch (IOException e) {
                        throw new CompletionException(e);
                    }
                })
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        switch (response.failed) {
            case 0:
                ts = String.valueOf(response.ts);
                updateUri();

                val updates = response.updates;

                logger.debug("Received {} updates", updates.size());

                for (val update : updates) {
                    try {
                        updateHandler.accept(update.getObject());
                    } catch (Exception e) {
                        logger.error("Cannot handle update", e);
                    }
                }
                break;
            case 1:
                ts = String.valueOf(response.ts);
                updateUri();
                break;
            case 2:
            case 3:
                updateServer();
        }
    }

}
