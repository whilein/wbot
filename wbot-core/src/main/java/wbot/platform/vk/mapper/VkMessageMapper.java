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

package wbot.platform.vk.mapper;

import lombok.val;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import wbot.model.InKeyboardCallback;
import wbot.model.InMessage;
import wbot.platform.vk.model.Message;
import wbot.platform.vk.model.update.MessageEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author whilein
 */
@Mapper
public interface VkMessageMapper {
    VkMessageMapper INSTANCE = Mappers.getMapper(VkMessageMapper.class);

    @Mapping(target = "id", source = "conversationMessageId")
    @Mapping(target = "reply", source = "message", qualifiedByName = "mapFirstForwardedMessage")
    @Mapping(target = "forwarded", source = "message", qualifiedByName = "mapForwardedMessages")
    @Mapping(target = "from", source = "fromId")
    @Mapping(target = "chat", source = "peerId")
    @Mapping(target = "ref", source = ".", qualifiedByName = "mapToRef")
    InMessage mapToMessage(Message message);

    @Mapping(target = "replyMessageId", source = "conversationMessageId")
    @Mapping(target = "from", source = "fromId")
    @Mapping(target = "chat", source = "peerId")
    @Mapping(target = "data", source = "payload")
    InKeyboardCallback mapToKeyboardCallback(Message message);

    @Mapping(target = "replyMessageId", source = "conversationMessageId")
    @Mapping(target = "from", source = "userId")
    @Mapping(target = "chat", source = "peerId")
    @Mapping(target = "data", expression = "java(String.valueOf(messageEvent.getPayload()))")
    InKeyboardCallback mapToKeyboardCallback(MessageEvent messageEvent);

    @Named("mapFirstForwardedMessage")
    default InMessage mapFirstForwardedMessage(Message message) {
        val fwdMessages = message.getFwdMessages();
        if (fwdMessages == null || fwdMessages.isEmpty()) {
            return mapToMessage(message.getReplyMessage());
        }

        return mapToMessage(fwdMessages.get(0));
    }

    @Named("mapForwardedMessages")
    default List<InMessage> mapForwardedMessages(Message message) {
        val fwdMessages = message.getFwdMessages();
        if (fwdMessages != null && !fwdMessages.isEmpty()) {
            val result = new ArrayList<InMessage>(fwdMessages.size());
            for (val fwdMessage : fwdMessages) {
                result.add(mapToMessage(fwdMessage));
            }

            return result;
        }

        Message replyMessage;
        if ((replyMessage = message.getReplyMessage()) != null) {
            return Collections.singletonList(mapToMessage(replyMessage));
        }

        return Collections.emptyList();
    }

    @Named("mapToRef")
    default Object mapToRef(Message message) {
        return message;
    }

}
