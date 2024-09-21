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

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import wbot.model.InlineKeyboard;
import wbot.model.InlineKeyboardButton;
import wbot.platform.vk.model.Keyboard;
import wbot.platform.vk.model.KeyboardButton;
import wbot.platform.vk.model.KeyboardTextAction;

import java.util.List;

/**
 * @author whilein
 */
@Mapper
public interface VkInlineKeyboardMapper {
    VkInlineKeyboardMapper INSTANCE = Mappers.getMapper(VkInlineKeyboardMapper.class);

    @Mapping(target = "inline", constant = "true")
    @Mapping(target = "oneTime", constant = "false")
    Keyboard mapKeyboard(InlineKeyboard keyboard);

    List<KeyboardButton> mapKeyboardButtons(List<InlineKeyboardButton> keyboardButtons);

    @Mapping(target = "action", source = ".")
    KeyboardButton mapKeyboardButton(InlineKeyboardButton keyboardButton);

    @Mapping(target = "payload", source = "data")
    KeyboardTextAction mapKeyboardAction(InlineKeyboardButton keyboardButton);
}
