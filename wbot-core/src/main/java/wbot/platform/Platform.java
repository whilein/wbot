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

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import wbot.http.HttpResponse;
import wbot.model.Attachment;
import wbot.model.IdentityHolder;
import wbot.model.IdentityName;
import wbot.model.ImageDimensions;
import wbot.model.InKeyboardCallback;
import wbot.model.InMessage;
import wbot.model.OutMessage;
import wbot.model.Photo;
import wbot.model.PhotoSize;
import wbot.model.SentMessage;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

/**
 * @author whilein
 */
public interface Platform extends Runnable {

    Logger getLogger();

    IdentityHolder getIdentity();

    PlatformType getType();

    CompletableFuture<SentMessage> sendMessage(OutMessage message);

    CompletableFuture<Void> editText(SentMessage message, String text);

    CompletableFuture<Void> editAttachment(SentMessage message, Attachment attachment);

    CompletableFuture<Void> editMessage(SentMessage message, OutMessage newMessage);

    CompletableFuture<Void> editMessage(InKeyboardCallback message, OutMessage newMessage);

    CompletableFuture<IdentityName> getName(IdentityHolder identity);

    CompletableFuture<HttpResponse> getAvatar(IdentityHolder identity, PhotoSize photoSize);

    String formatLinkToIdentity(IdentityHolder identity);

    CompletableFuture<Photo> getPhoto(InMessage message, Predicate<ImageDimensions> filter,
                                      Comparator<ImageDimensions> sizeComparator);

    CompletableFuture<Void> answerCallback(InKeyboardCallback keyboardCallback, @Nullable String text);

}
