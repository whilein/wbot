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
import wbot.model.InKeyboardCallback;
import wbot.model.InlineKeyboard;
import wbot.model.OutMessage;
import wbot.model.PhotoSize;
import wbot.model.SentMessage;
import wbot.platform.Platform;
import wbot.platform.PlatformType;
import wbot.platform.telegram.mapper.TelegramInlineKeyboardMapper;
import wbot.platform.telegram.mapper.TelegramMessageMapper;
import wbot.platform.telegram.method.TelegramEdit;
import wbot.platform.telegram.method.TelegramSend;
import wbot.platform.telegram.model.CallbackQuery;
import wbot.platform.telegram.model.Chat;
import wbot.platform.telegram.model.File;
import wbot.platform.telegram.model.Message;
import wbot.platform.telegram.model.Update;
import wbot.platform.telegram.model.User;
import wbot.util.FutureUtils;

import java.util.concurrent.CompletableFuture;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class TelegramPlatform implements Platform {

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
            sendCallbackAnswer(query);
        }
    }

    private void sendCallbackAnswer(CallbackQuery query) {
        telegramClient.answerCallbackQuery()
                .queryId(query.getId())
                .make();
    }

    @Override
    public CompletableFuture<IdentityName> getName(IdentityHolder identity) {
        if (identity instanceof User) {
            val user = (User) identity;
            return CompletableFuture.completedFuture(new IdentityName(user.getFirstName(), user.getLastName()));
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
                ? CompletableFuture.completedFuture(telegramClient.sendMessage().text(message.getText()))
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
        val oldAttachment = message.getOutMessage().getAttachment();
        val chatId = message.getOutMessage().getChat().getValue();

        return FutureUtils.asVoid(makeMinimalEdit(chatId, message.getMessageId(), text, oldAttachment, null));
    }

    @Override
    public CompletableFuture<Void> editMessage(SentMessage message, OutMessage newMessage) {
        val oldAttachment = message.getOutMessage().getAttachment();
        val chatId = message.getOutMessage().getChat().getValue();

        return makeMinimalEdit(chatId, message.getMessageId(),
                newMessage.getText(),
                oldAttachment,
                newMessage.getKeyboard()
        ).thenCompose(m -> {
            if (newMessage.hasAttachment()) {
                // not supported.
                logger.warn("Editing a message with an attachment is not supported correctly on this platform.");
//                return editAttachment(message, newMessage.getAttachment());
            }

            return CompletableFuture.completedFuture(null);
        });
    }

    @Override
    public CompletableFuture<Void> editMessage(InKeyboardCallback message, OutMessage newMessage) {
        val messageId = message.getReplyMessageId();
        val chatId = message.getChat().getValue();

        return FutureUtils.asVoid(makeMinimalEdit(chatId, messageId,
                newMessage.getText(),
                null,
                newMessage.getKeyboard()));
    }

    private CompletableFuture<Message> makeMinimalEdit(
            long chatId,
            long messageId,
            String text,
            @Nullable Attachment oldAttachment,
            @Nullable InlineKeyboard keyboard
    ) {
        TelegramEdit<?> edit;
        if (oldAttachment == null) {
            edit = telegramClient.editMessageText()
                    .text(text);
        } else {
            edit = telegramClient.editMessageCaption()
                    .caption(text);
        }

        if (keyboard != null) {
            edit.replyMarkup(TelegramInlineKeyboardMapper.INSTANCE.mapKeyboard(keyboard));
        }

        return edit.messageId(messageId)
                .chatId(chatId)
                .make();
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
