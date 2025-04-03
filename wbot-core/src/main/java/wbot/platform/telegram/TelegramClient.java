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

package wbot.platform.telegram;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import wbot.http.HttpClient;
import wbot.http.HttpResponse;
import wbot.platform.telegram.method.TelegramAnswerCallbackQuery;
import wbot.platform.telegram.method.TelegramEditMessageCaption;
import wbot.platform.telegram.method.TelegramEditMessageMedia;
import wbot.platform.telegram.method.TelegramEditMessageText;
import wbot.platform.telegram.method.TelegramGetChat;
import wbot.platform.telegram.method.TelegramGetFile;
import wbot.platform.telegram.method.TelegramGetMe;
import wbot.platform.telegram.method.TelegramGetUpdates;
import wbot.platform.telegram.method.TelegramGetUserProfilePhotos;
import wbot.platform.telegram.method.TelegramMethod;
import wbot.platform.telegram.method.TelegramSendDocument;
import wbot.platform.telegram.method.TelegramSendMessage;
import wbot.platform.telegram.method.TelegramSendPhoto;
import wbot.platform.telegram.model.ResponseOrError;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class TelegramClient {

    private static final String BOT_API_URL = "https://api.telegram.org/bot";
    private static final String BOT_FILES_API_URL = "https://api.telegram.org/file/bot";

    String apiUrl;

    String fileApiUrl;

    @Getter
    HttpClient httpClient;

    @Getter
    JsonMapper jsonMapper;

    public TelegramClient(String token, HttpClient httpClient, JsonMapper jsonMapper) {
        this.apiUrl = BOT_API_URL + token;
        this.fileApiUrl = BOT_FILES_API_URL + token;
        this.httpClient = httpClient;
        this.jsonMapper = jsonMapper;
    }

    public TelegramEditMessageText editMessageText() {
        return new TelegramEditMessageText(this);
    }

    public TelegramEditMessageCaption editMessageCaption() {
        return new TelegramEditMessageCaption(this);
    }

    public TelegramEditMessageMedia editMessageMedia() {
        return new TelegramEditMessageMedia(this);
    }

    public TelegramGetMe getMe() {
        return new TelegramGetMe(this);
    }

    public TelegramGetUpdates getUpdates() {
        return new TelegramGetUpdates(this);
    }

    public TelegramSendPhoto sendPhoto() {
        return new TelegramSendPhoto(this);
    }

    public TelegramSendDocument sendDocument() {
        return new TelegramSendDocument(this);
    }

    public TelegramSendMessage sendMessage() {
        return new TelegramSendMessage(this);
    }

    public TelegramAnswerCallbackQuery answerCallbackQuery() {
        return new TelegramAnswerCallbackQuery(this);
    }

    public TelegramGetUserProfilePhotos getUserProfilePhotos() {
        return new TelegramGetUserProfilePhotos(this);
    }

    public TelegramGetFile getFile() {
        return new TelegramGetFile(this);
    }

    public TelegramGetChat getChat() {
        return new TelegramGetChat(this);
    }

    public <R> CompletableFuture<R> send(TelegramMethod<R> method) {
        val responseType = jsonMapper.getTypeFactory().constructParametricType(ResponseOrError.class, method.type());

        return httpClient.post(apiUrl + "/" + method.name(), method.params().asContent())
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

    public CompletableFuture<HttpResponse> getFile(String path) {
        return httpClient.get(getUrlToFile(path));
    }

    public String getUrlToFile(String path) {
        return fileApiUrl + "/" + path;
    }

}
