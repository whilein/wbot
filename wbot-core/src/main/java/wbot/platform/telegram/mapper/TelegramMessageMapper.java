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

package wbot.platform.telegram.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import wbot.model.InKeyboardCallback;
import wbot.model.InMessage;
import wbot.platform.telegram.model.CallbackQuery;
import wbot.platform.telegram.model.Message;

/**
 * @author whilein
 */
@Mapper
public interface TelegramMessageMapper {
    TelegramMessageMapper INSTANCE = Mappers.getMapper(TelegramMessageMapper.class);

    @Mapping(target = "id", source = "messageId")
    @Mapping(target = "reply", source = "replyToMessage")
    InMessage mapToMessage(Message message);

    @Mapping(target = "replyMessageId", source = "message.messageId")
    @Mapping(target = "chat", source = "message.chat")
    InKeyboardCallback mapToKeyboardCallback(CallbackQuery callbackQuery);

}
