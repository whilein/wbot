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
import wbot.model.InlineKeyboard;
import wbot.platform.telegram.model.InlineKeyboardButton;
import wbot.platform.telegram.model.InlineKeyboardMarkup;

import java.util.List;

/**
 * @author whilein
 */
@Mapper
public interface TelegramInlineKeyboardMapper {
    TelegramInlineKeyboardMapper INSTANCE = Mappers.getMapper(TelegramInlineKeyboardMapper.class);

    @Mapping(target = "inlineKeyboard", source = "buttons")
    InlineKeyboardMarkup mapKeyboard(InlineKeyboard keyboard);

    List<InlineKeyboardButton> mapKeyboardButtons(List<wbot.model.InlineKeyboardButton> keyboardButtons);

    @Mapping(target = "text", source = "label")
    @Mapping(target = "callbackData", source = "data")
    InlineKeyboardButton mapKeyboardButton(wbot.model.InlineKeyboardButton keyboardButton);

}
