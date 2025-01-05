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

package wbot.platform;

import wbot.http.HttpResponse;
import wbot.model.Attachment;
import wbot.model.IdentityHolder;
import wbot.model.IdentityName;
import wbot.model.InKeyboardCallback;
import wbot.model.OutMessage;
import wbot.model.PhotoSize;
import wbot.model.SentMessage;

import java.util.concurrent.CompletableFuture;

/**
 * @author whilein
 */
public interface Platform extends Runnable {

    PlatformType getType();

    CompletableFuture<SentMessage> sendMessage(OutMessage message);

    CompletableFuture<Void> editText(SentMessage message, String text);

    CompletableFuture<Void> editAttachment(SentMessage message, Attachment attachment);

    CompletableFuture<Void> editMessage(SentMessage message, OutMessage newMessage);

    CompletableFuture<Void> editMessage(InKeyboardCallback message, OutMessage newMessage);

    CompletableFuture<IdentityName> getName(IdentityHolder identity);

    CompletableFuture<HttpResponse> getAvatar(IdentityHolder identity, PhotoSize photoSize);

}
