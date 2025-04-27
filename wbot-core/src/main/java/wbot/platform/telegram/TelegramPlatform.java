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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import wbot.event.EventDispatcher;
import wbot.http.HttpResponse;
import wbot.model.Attachment;
import wbot.model.IdentityHolder;
import wbot.model.IdentityName;
import wbot.model.ImageDimensions;
import wbot.model.InKeyboardCallback;
import wbot.model.InMessage;
import wbot.model.InlineKeyboard;
import wbot.model.OutMessage;
import wbot.model.Photo;
import wbot.model.PhotoSize;
import wbot.model.SentMessage;
import wbot.platform.Platform;
import wbot.platform.PlatformType;
import wbot.platform.telegram.mapper.TelegramInlineKeyboardMapper;
import wbot.platform.telegram.mapper.TelegramMessageMapper;
import wbot.platform.telegram.method.TelegramSend;
import wbot.platform.telegram.model.CallbackQuery;
import wbot.platform.telegram.model.Chat;
import wbot.platform.telegram.model.File;
import wbot.platform.telegram.model.Message;
import wbot.platform.telegram.model.Update;
import wbot.platform.telegram.model.User;
import wbot.util.FutureUtils;

import java.io.InputStream;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class TelegramPlatform implements Platform {

    @Getter
    Logger logger;

    @Getter
    TelegramClient telegramClient;

    EventDispatcher eventDispatcher;

    @Getter
    @NonFinal
    IdentityHolder identity;

    private void handleUpdate(Update update) {
        Message message;
        if ((message = update.getMessage()) != null) {
            eventDispatcher.message(this, TelegramMessageMapper.INSTANCE.mapToMessage(message));
        }

        CallbackQuery query;
        if ((query = update.getCallbackQuery()) != null) {
            eventDispatcher.keyboardCallback(this, TelegramMessageMapper.INSTANCE.mapToKeyboardCallback(query));
        }
    }

    @Override
    public CompletableFuture<IdentityName> getName(IdentityHolder identity) {
        if (identity instanceof User) {
            val user = (User) identity;
            return completedFuture(new IdentityName(user.getFirstName(), user.getLastName()));
        } else if (identity instanceof Chat) {
            throw new IllegalArgumentException("Cannot get display name of chat identity");
        } else {
            throw new IllegalArgumentException("Identity platform is not Telegram");
        }
    }

    @Override
    public CompletableFuture<HttpResponse> getAvatar(IdentityHolder identity, PhotoSize photoSize) {
        if (identity.isChat()) {
            throw new IllegalArgumentException("Cannot get avatar of chat identity");
        }

        if (!(identity instanceof User)) {
            throw new IllegalArgumentException("Identity platform is not Telegram");
        }

        return telegramClient.getUserProfilePhotos()
                .userId(identity.getValue())
                .limit(PhotoSize.VALUES.size())
                .make()
                .thenCompose(profile -> {
                    val photos = profile.getPhotos();
                    if (photos.isEmpty()) return completedFuture(new HttpResponse(InputStream.nullInputStream()));

                    val sizes = profile.getPhotos().get(0);
                    val firstPhoto = sizes.get(photoSize.ordinal());

                    return telegramClient.getFile()
                            .fileId(firstPhoto.getFileId())
                            .make()
                            .thenApply(File::getFilePath)
                            .thenCompose(telegramClient::getFile);
                });
    }

    @Override
    public String formatLinkToIdentity(IdentityHolder identity) {
        if (!(identity instanceof User)) {
            throw new IllegalArgumentException("Identity platform is not Telegram");
        }

        return "https://t.me/" + ((User) identity).getUsername();
    }

    @Override
    public PlatformType getType() {
        return PlatformType.TELEGRAM;
    }

    private CompletableFuture<TelegramSend<?>> sendAttachment(Attachment attachment, String caption) {
        return attachment.createContent()
                .thenApply(content -> {
                    switch (attachment.type()) {
                        case PHOTO:
                            return telegramClient.sendPhoto().photo(attachment.fileName(), content)
                                    .caption(caption);
                        case DOCUMENT:
                            return telegramClient.sendDocument().document(attachment.fileName(), content)
                                    .caption(caption);
                        default:
                            throw new IllegalArgumentException("Unsupported attachment type");
                    }
                });
    }

    @Override
    public CompletableFuture<SentMessage> sendMessage(OutMessage message) {
        val peer = message.getChat();
        if (peer.getPlatform() != PlatformType.TELEGRAM) {
            throw new IllegalArgumentException("Chat platform is not Telegram");
        }

        val attachment = message.getAttachment();

        CompletableFuture<TelegramSend<?>> sendFuture = attachment == null
                ? completedFuture(telegramClient.sendMessage().text(message.getText()))
                : sendAttachment(attachment, message.getText());

        return sendFuture
                .thenCompose(send -> {
                    send.chatId(peer.getValue());
                    send.disableNotification(message.isDisableNotification());

                    val reply = message.getReply();
                    if (reply != null) {
                        send.replyToMessageId(reply);
                    }

                    val keyboard = message.getKeyboard();
                    if (keyboard != null) {
                        send.replyMarkup(TelegramInlineKeyboardMapper.INSTANCE.mapKeyboard(keyboard));
                    }

                    return send.make();
                })
                .thenApply(m -> new SentMessage(this, m.getMessageId(), m.getMessageId(), message));
    }

    @Override
    public CompletableFuture<Void> editAttachment(SentMessage message, Attachment attachment) {
        val type = attachment.type().toString().toLowerCase();

        return FutureUtils.asVoid(attachment.createContent()
                .thenCompose(content -> telegramClient.editMessageMedia()
                        .messageId(message.getMessageId())
                        .chatId(message.getOutMessage().getChat().getValue())
                        .media(type, attachment.fileName(), content)
                        .make()));
    }

    @Override
    public CompletableFuture<Void> editText(SentMessage message, String text) {
        val oldMessage = message.getOutMessage();

        return editMessageContent(message.getMessageId(), oldMessage.getChat().getValue(),
                text,
                oldMessage.getAttachment(),
                null,
                null);
    }

    @Override
    public CompletableFuture<Void> editMessage(SentMessage message, OutMessage newMessage) {
        val oldMessage = message.getOutMessage();

        return editMessageContent(message.getMessageId(), oldMessage.getChat().getValue(),
                newMessage.getText(),
                oldMessage.getAttachment(),
                newMessage.getAttachment(),
                newMessage.getKeyboard());
    }

    @Override
    public CompletableFuture<Void> editMessage(InKeyboardCallback keyboardCallback, OutMessage newMessage) {
        return editMessageContent(keyboardCallback.getReplyMessageId(), keyboardCallback.getChat().getValue(),
                newMessage.getText(),
                null,
                newMessage.getAttachment(),
                newMessage.getKeyboard());
    }

    private CompletableFuture<Void> editMessageContent(
            long messageId,
            long chatId,
            String text,
            Attachment oldAttachment,
            Attachment attachment,
            InlineKeyboard keyboard
    ) {
        if (attachment != null) {
            val editMessageMedia = telegramClient.editMessageMedia()
                    .messageId(messageId)
                    .chatId(chatId);

            if (keyboard != null) {
                editMessageMedia.replyMarkup(TelegramInlineKeyboardMapper.INSTANCE.mapKeyboard(keyboard));
            }

            val type = attachment.type().toString().toLowerCase();

            return FutureUtils.asVoid(attachment.createContent()
                    .thenCompose(content -> editMessageMedia
                            .media(type, attachment.fileName(), content, text)
                            .make()));
        }

        val telegramEdit = (oldAttachment == null)
                ? telegramClient.editMessageText().text(text)
                : telegramClient.editMessageCaption().caption(text);

        if (keyboard != null) {
            telegramEdit.replyMarkup(TelegramInlineKeyboardMapper.INSTANCE.mapKeyboard(keyboard));
        }

        return FutureUtils.asVoid(telegramEdit.messageId(messageId)
                .chatId(chatId)
                .make());
    }

    @Override
    public CompletableFuture<Photo> getPhoto(
            InMessage message,
            Predicate<ImageDimensions> filter,
            Comparator<ImageDimensions> maxComparator
    ) {
        if (!(message.getRef() instanceof Message)) {
            throw new IllegalArgumentException("Source platform is not Telegram");
        }

        val ref = (Message) message.getRef();
        val photo = ref.getPhoto();
        if (photo == null) {
            return completedFuture(null);
        }

        val bestPhoto = photo.stream()
                .map(TelegramPlatformPhotoDimensions::new)
                .filter(filter)
                .max(maxComparator)
                .orElse(null);

        if (bestPhoto == null) {
            return completedFuture(null);
        }

        val photoSize = bestPhoto.photoSize;
        return telegramClient.getFile()
                .fileId(photoSize.getFileId())
                .make()
                .thenApply(file -> new Photo(
                        telegramClient.getUrlToFile(file.getFilePath()),
                        photoSize.getWidth(),
                        photoSize.getHeight()));
    }

    @Override
    public CompletableFuture<Void> answerCallback(InKeyboardCallback keyboardCallback, @Nullable String text) {
        val answerCallbackQuery = telegramClient.answerCallbackQuery()
                .queryId(keyboardCallback.getId());

        if (text != null) {
            if (text.length() > 200) {
                throw new IllegalArgumentException(String.format("Text too long, %s > 200", text.length()));
            }

            answerCallbackQuery.text(text);
        }

        return FutureUtils.asVoid(answerCallbackQuery.make());
    }

    @FieldDefaults(makeFinal = true)
    @RequiredArgsConstructor
    private static class TelegramPlatformPhotoDimensions implements ImageDimensions {
        wbot.platform.telegram.model.PhotoSize photoSize;

        @Override
        public int getWidth() {
            return photoSize.getWidth();
        }

        @Override
        public int getHeight() {
            return photoSize.getHeight();
        }
    }

    @Override
    public void run() {
        try {
            val user = telegramClient.getMe()
                    .make()
                    .get();

            this.identity = user;

            logger.info("Waiting for updates in bot @" + user.getUsername() + " (id: " + user.getId() + ")");

            val telegram = new TelegramLongPoll(logger, telegramClient);
            telegram.start(this::handleUpdate);
        } catch (Exception e) {
            logger.error("Failed to start Telegram LongPoll", e);
        }
    }
}
