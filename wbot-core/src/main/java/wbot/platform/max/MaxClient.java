/*
 *    Copyright 2025 Whilein
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

package wbot.platform.max;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import wbot.http.HttpClient;
import wbot.platform.telegram.TelegramException;
import wbot.platform.telegram.model.ResponseOrError;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * @author _Novit_ (novitpw)
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class MaxClient {
    private static final String ENDPOINT = "https://botapi.max.ru/";
    private static final String VERSION = "0.0.6";

    @Getter
    String defaultParams;

    @Getter
    HttpClient httpClient;

    @Getter
    JsonMapper jsonMapper;

    public MaxClient(String token, HttpClient httpClient, JsonMapper jsonMapper) {
        this.defaultParams = "access_token=" + token
                             + "&v=" + VERSION;

        this.httpClient = httpClient;
        this.jsonMapper = jsonMapper;
    }

    public <R> CompletableFuture<R> send(MaxMethod<R> method) {
        val responseType = jsonMapper.getTypeFactory().constructParametricType(ResponseOrError.class, method.type());

        return httpClient.post(ENDPOINT + "/" + method.name(), method.params().asContent())
                .thenApply(response -> {
                    ResponseOrError<R> responseOrError;

                    try {
                        responseOrError = jsonMapper.readValue(response.getContent(), responseType);
                    } catch (IOException e) {
                        throw new CompletionException(e);
                    }

                    if (!responseOrError.isOk()) {
                        throw new TelegramException("[" + responseOrError.getErrorCode() + "] " + responseOrError.getDescription());
                    }

                    return responseOrError.getResult();
                });
    }

}
