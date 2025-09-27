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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
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
import wbot.platform.Platform;
import wbot.platform.PlatformType;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

/**
 * @author _Novit_ (novitpw)
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class MaxPlatform implements Platform {

    @Getter
    Logger logger;

    @Getter
    MaxClient maxClient;

    @Getter
    @NonFinal
    IdentityHolder identity;

    @Override
    public PlatformType getType() {
        return PlatformType.MAX;
    }

    @Override
    public CompletableFuture<SentMessage> sendMessage(OutMessage message) {
        return null;
    }

    @Override
    public CompletableFuture<Void> editText(SentMessage message, String text) {
        return null;
    }

    @Override
    public CompletableFuture<Void> editAttachment(SentMessage message, Attachment attachment) {
        return null;
    }

    @Override
    public CompletableFuture<Void> editMessage(SentMessage message, OutMessage newMessage) {
        return null;
    }

    @Override
    public CompletableFuture<Void> editMessage(InKeyboardCallback message, OutMessage newMessage) {
        return null;
    }

    @Override
    public CompletableFuture<IdentityName> getName(IdentityHolder identity) {
        return null;
    }

    @Override
    public CompletableFuture<HttpResponse> getAvatar(IdentityHolder identity, PhotoSize photoSize) {
        return null;
    }

    @Override
    public String formatLinkToIdentity(IdentityHolder identity) {
        return "";
    }

    @Override
    public CompletableFuture<Photo> getPhoto(InMessage message, Predicate<ImageDimensions> filter, Comparator<ImageDimensions> sizeComparator) {
        return null;
    }

    @Override
    public CompletableFuture<Void> answerCallback(InKeyboardCallback keyboardCallback, @Nullable String text) {
        return null;
    }

    @Override
    public void run() {

    }
}
