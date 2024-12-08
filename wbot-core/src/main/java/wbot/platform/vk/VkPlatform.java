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

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import wbot.event.EventDispatcher;
import wbot.http.EmbeddableContent;
import wbot.http.MultipartContent;
import wbot.model.Attachment;
import wbot.model.IdentityHolder;
import wbot.model.OutMessage;
import wbot.model.SentMessage;
import wbot.platform.Platform;
import wbot.platform.PlatformType;
import wbot.platform.vk.mapper.VkInlineKeyboardMapper;
import wbot.platform.vk.mapper.VkMessageMapper;
import wbot.platform.vk.method.VkDocsSave;
import wbot.platform.vk.method.VkMessagesEdit;
import wbot.platform.vk.method.VkMessagesSend;
import wbot.platform.vk.method.VkMethod;
import wbot.platform.vk.model.Document;
import wbot.platform.vk.model.Forward;
import wbot.platform.vk.model.Group;
import wbot.platform.vk.model.Id;
import wbot.platform.vk.model.Photo;
import wbot.platform.vk.model.User;
import wbot.platform.vk.model.update.MessageNew;
import wbot.platform.vk.model.update.UpdateObject;
import wbot.util.FutureUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class VkPlatform implements Platform {

    long documentOwnerId;

    Logger logger;

    VkClient vkClient;
    EventDispatcher eventDispatcher;

    private void handleUpdate(UpdateObject object) {
        if (object instanceof MessageNew) {
            val message = ((MessageNew) object).getMessage();

            if (message.getPayload() != null) {
                eventDispatcher.keyboardCallback(this, VkMessageMapper.INSTANCE.mapToKeyboardCallback(message));
            } else {
                eventDispatcher.message(this, VkMessageMapper.INSTANCE.mapToMessage(message));
            }
        }
    }

    @Value
    @Builder
    @Jacksonized
    private static class PhotoUploadResult {
        int server;
        String hash;
        String photo;
    }

    @Value
    @Builder
    @Jacksonized
    private static class DocumentUploadResult {
        String file;
    }

    private <T> CompletableFuture<T> upload(String fileName, EmbeddableContent file, String uploadUrl, Class<T> type) {
        val httpClient = vkClient.getHttpClient();

        val requestContent = new MultipartContent.Builder()
                .addPart(new MultipartContent.Part("file", fileName, file))
                .build();

        return httpClient.post(uploadUrl, requestContent)
                .thenApply(response -> {
                    try (val content = response.getContent()) {
                        return vkClient.getJsonMapper().readValue(content, type);
                    } catch (IOException e) {
                        throw new CompletionException(e);
                    }
                });
    }

    private <T> CompletableFuture<T> upload(Attachment attachment, String uploadUrl, Class<T> type) {
        return attachment.createContent()
                .thenCompose(content -> upload(attachment.fileName(), content, uploadUrl, type));
    }

    private CompletableFuture<Document> uploadDocument(Attachment document) {
        return vkClient.docsGetMessagesUploadServer().peerId(documentOwnerId).make()
                .thenCompose(result -> upload(document, result.getUploadUrl(), DocumentUploadResult.class))
                .thenCompose(result -> vkClient.docsSave()
                        .file(result.file)
                        .make())
                .thenApply(VkDocsSave.Result::getDoc);
    }


    private CompletableFuture<Photo> uploadPhoto(Attachment photo) {
        return vkClient.photosGetMessagesUploadServer().make()
                .thenCompose(result -> upload(photo, result.getUploadUrl(), PhotoUploadResult.class))
                .thenCompose(result -> vkClient.photosSaveMessagesPhoto()
                        .server(result.server)
                        .hash(result.hash)
                        .photo(result.photo)
                        .make())
                .thenApply(photos -> photos[0])
                .whenComplete((r, e) -> {
                    if (e != null) logger.error("", e);
                });
    }

    @Override
    public PlatformType getType() {
        return PlatformType.VK;
    }

    @Override
    public CompletableFuture<SentMessage> sendMessage(OutMessage message) {
        val chat = message.getChat();
        if (chat.getPlatform() != PlatformType.VK) {
            throw new IllegalArgumentException("Chat platform is not VK");
        }

        val peerId = chat.getValue();

        val sendMessage = vkClient.messagesSend()
                .peerIds(peerId)
                .message(message.getText());

        val reply = message.getReply();
        if (reply != null) {
            sendMessage.forward(Forward.builder()
                    .isReply(true)
                    .peerId(peerId)
                    .conversationMessageIds(Collections.singletonList(reply))
                    .build());
        }

        val keyboard = message.getKeyboard();
        if (keyboard != null) {
            sendMessage.keyboard(VkInlineKeyboardMapper.INSTANCE.mapKeyboard(keyboard));
        }

        Attachment attachment;

        CompletableFuture<VkMessagesSend.Result[]> cf;
        if ((attachment = message.getAttachment()) != null) {
            cf = uploadAttachment(attachment)
                    .thenCompose(attachmentValue -> sendMessage.attachment(attachmentValue)
                            .make());
        } else {
            cf = sendMessage.make();
        }

        return cf.thenApply(results -> {
            val result = results[0];
            return new SentMessage(this,
                    result.getMessageId(),
                    result.getCmId(),
                    message
            );
        });
    }

    private CompletableFuture<String> uploadAttachment(Attachment attachment) {
        switch (attachment.type()) {
            case PHOTO:
                return uploadPhoto(attachment)
                        .thenApply(photo -> "photo" + photo.getOwnerId() + "_" + photo.getId());
            case DOCUMENT:
                return uploadDocument(attachment)
                        .thenApply(doc -> "doc" + doc.getOwnerId() + "_" + doc.getId());
            default:
                throw new IllegalArgumentException("Unsupported attachment type " + attachment.type());
        }
    }

    @Override
    public CompletableFuture<Void> editAttachment(SentMessage message, Attachment attachment) {
        return FutureUtils.asVoid(uploadAttachment(attachment)
                .thenCompose(v -> makeMinimalMessageEdit(message, null, attachment)));
    }

    @Override
    public CompletableFuture<Void> editText(SentMessage message, String newText) {
        return FutureUtils.asVoid(makeMinimalMessageEdit(message, newText, null));
    }

    @Override
    public CompletableFuture<Void> editMessage(SentMessage message, OutMessage newMessage) {
        return FutureUtils.asVoid(makeMessageEdit(message, newMessage));
    }

    private CompletableFuture<Integer> makeMessageEdit(SentMessage message, OutMessage newMessage) {
        return newMinimalMessageEdit(message, newMessage.getText(), newMessage.getAttachment())
                .thenCompose(messagesEdit -> {
                    val keyboard = newMessage.getKeyboard();
                    if (keyboard != null) {
                        messagesEdit.keyboard(VkInlineKeyboardMapper.INSTANCE.mapKeyboard(keyboard));
                    }

                    if (newMessage.isKeepForwardedMessages()) {
                        messagesEdit.keepForwardMessages(true);
                    }

                    if (newMessage.isDisableNotification()) {
                        messagesEdit.disableMentions(true);
                    }

                    return messagesEdit.make();
                });
    }

    private CompletableFuture<Integer> makeMinimalMessageEdit(
            SentMessage message,
            @Nullable String newText,
            @Nullable Attachment attachment
    ) {
        return newMinimalMessageEdit(message, newText, attachment)
                .thenCompose(VkMethod::make);
    }

    private CompletableFuture<VkMessagesEdit> newMinimalMessageEdit(
            SentMessage message,
            @Nullable String text,
            @Nullable Attachment attachment
    ) {
        val messagesEdit = vkClient.messagesEdit()
                .peerId(message.getOutMessage().getChat().getValue());

        val chatMessageId = message.getChatMessageId();
        if (chatMessageId != null) {
            messagesEdit.conversationMessageId(chatMessageId);
        } else {
            messagesEdit.messageId(message.getMessageId());
        }

        if (text != null) {
            messagesEdit.message(text);
        }

        if (attachment != null) {
            return uploadAttachment(attachment)
                    .thenApply(messagesEdit::attachment);
        }

        return CompletableFuture.completedFuture(messagesEdit);
    }

    private static String getUserName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }

    private static String getGroupName(Group group) {
        return group.getName();
    }

    @Override
    public CompletableFuture<String> getName(IdentityHolder identity) {
        if (identity.isChat()) {
            throw new IllegalArgumentException("Cannot get display name of chat identity");
        }

        if (!(identity instanceof Id)) {
            throw new IllegalArgumentException("Identity platform is not VK");
        }

        if (identity.isBot()) {
            return vkClient.groupsGetById()
                    .groupId(identity.getValue())
                    .make()
                    .thenApply(r -> getGroupName(r.getGroups()[0]));
        } else {
            return vkClient.usersGet()
                    .userIds(identity.getValue())
                    .make()
                    .thenApply(r -> getUserName(r[0]));
        }
    }

    @Override
    public void run() {
        try {
            val group = vkClient.groupsGetById()
                    .make()
                    .get()
                    .getGroups()[0];

            try {
                val settings = vkClient.groupsGetLongPollSettings()
                        .groupId(group.getId())
                        .make()
                        .get();

                if (!settings.isEnabled()) {
                    logger.error("LongPoll API is disabled! To enable, follow the link: https://vk.com/club" + group.getId()
                                 + "?act=longpoll_api");
                    return;
                }

                if (!settings.getEvents().hasAny()) {
                    logger.error("LongPoll API has no enabled event types. To enable, follow the link: https://vk.com/club" + group.getId()
                                 + "?act=longpoll_api_types");
                    return;
                }
            } catch (VkException e) {
                logger.error("Cannot check for group settings", e);
            }

            val name = Optional.ofNullable(group.getScreenName())
                    .map(screenName -> "@" + screenName + " (id: " + group.getId() + ")")
                    .orElseGet(() -> "@club" + group.getId());

            logger.info("Waiting for updates in group " + name);

            val longPoll = new VkLongPoll(logger, vkClient, group.getId());
            longPoll.start(this::handleUpdate);

            logger.info("LP terminated");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("Failed to start Vkontakte LongPoll", e);
        }
    }
}
