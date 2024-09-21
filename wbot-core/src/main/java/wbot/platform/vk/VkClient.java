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

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import wbot.http.HttpClient;
import wbot.platform.vk.method.VkDocsGetMessagesUploadServer;
import wbot.platform.vk.method.VkDocsSave;
import wbot.platform.vk.method.VkGroupsGetById;
import wbot.platform.vk.method.VkGroupsGetLongPollServer;
import wbot.platform.vk.method.VkGroupsGetLongPollSettings;
import wbot.platform.vk.method.VkMessagesEdit;
import wbot.platform.vk.method.VkMessagesSend;
import wbot.platform.vk.method.VkMethod;
import wbot.platform.vk.method.VkPhotosGetMessagesUploadServer;
import wbot.platform.vk.method.VkPhotosSaveMessagesPhoto;
import wbot.platform.vk.method.VkUsersGet;
import wbot.platform.vk.model.ResponseOrError;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * @author whilein
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class VkClient {

    private static final String API_URL = "https://api.vk.com/method/";
    private static final String API_VERSION = "5.199";

    String defaultParams;

    HttpClient httpClient;
    JsonMapper jsonMapper;

    public VkClient(String token, HttpClient httpClient, JsonMapper jsonMapper) {
        this.defaultParams = "access_token=" + token
                             + "&v=" + API_VERSION
                             + "&lang=ru";
        this.httpClient = httpClient;
        this.jsonMapper = jsonMapper;
    }


    public VkDocsGetMessagesUploadServer docsGetMessagesUploadServer() {
        return new VkDocsGetMessagesUploadServer(this);
    }

    public VkDocsSave docsSave() {
        return new VkDocsSave(this);
    }

    public VkPhotosGetMessagesUploadServer photosGetMessagesUploadServer() {
        return new VkPhotosGetMessagesUploadServer(this);
    }

    public VkPhotosSaveMessagesPhoto photosSaveMessagesPhoto() {
        return new VkPhotosSaveMessagesPhoto(this);
    }

    public VkUsersGet usersGet() {
        return new VkUsersGet(this);
    }

    public VkMessagesEdit messagesEdit() {
        return new VkMessagesEdit(this);
    }

    public VkMessagesSend messagesSend() {
        return new VkMessagesSend(this);
    }

    public VkGroupsGetById groupsGetById() {
        return new VkGroupsGetById(this);
    }

    public VkGroupsGetLongPollSettings groupsGetLongPollSettings() {
        return new VkGroupsGetLongPollSettings(this);
    }

    public VkGroupsGetLongPollServer groupsGetLongPollServer() {
        return new VkGroupsGetLongPollServer(this);
    }

    public <R> CompletableFuture<R> send(VkMethod<R> method) {
        val type = jsonMapper.getTypeFactory().constructParametricType(ResponseOrError.class, method.type());

        return httpClient.post(API_URL + method.name(), method.params().asContent())
                .thenApply(response -> {
                    ResponseOrError<R> responseOrError;

                    try (val content = response.getContent()) {
                        responseOrError = jsonMapper.readValue(content, type);
                    } catch (IOException e) {
                        throw new CompletionException(e);
                    }

                    val error = responseOrError.getError();
                    if (error != null) {
                        throw new VkException("[" + error.getErrorCode() + "] " + error.getErrorMsg());
                    }

                    return responseOrError.getResponse();
                });
    }

}
