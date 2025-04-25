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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.jackson.Jacksonized;
import lombok.val;
import org.slf4j.Logger;
import wbot.platform.vk.model.update.Update;
import wbot.platform.vk.model.update.UpdateObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class VkLongPoll {

    private static final String URL = "%s?act=a_check&key=%s&ts=%s&wait=90";

    Logger logger;

    VkClient vkClient;

    long groupId;

    @NonFinal
    String uri;

    @NonFinal
    String server, key, ts;

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

    public void start(Consumer<UpdateObject> updateHandler) throws InterruptedException {
        try {
            updateServer();
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }

        val httpClient = vkClient.getHttpClient();
        val jsonMapper = vkClient.getJsonMapper();

        while (true) {
            try {
                val response = httpClient.get(uri)
                        .thenApply(value -> {
                            try (val content = value.getContent()) {
                                return jsonMapper.readValue(content, Response.class);
                            } catch (IOException e) {
                                throw new CompletionException(e);
                            }
                        })
                        .get();

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
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.error("LongPoll receiving updates failure", e);
            }
        }
    }

}
