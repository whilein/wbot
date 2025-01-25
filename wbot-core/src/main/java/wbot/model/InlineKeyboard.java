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

package wbot.model;

import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.val;
import org.jetbrains.annotations.Unmodifiable;
import wbot.platform.vk.model.KeyboardAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author whilein
 */
@Value
public class InlineKeyboard {

    @Unmodifiable List<@Unmodifiable List<InlineKeyboardButton>> buttons;

    public static final class Builder {

        @NonFinal
        List<InlineKeyboardButton> rowButtons = new ArrayList<>();

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        public Builder button(String label, String data) {
            return button(label, data, null);
        }

        public Builder button(String label, String data, String color) {
            return button(label, data, color, KeyboardAction.Type.TEXT);
        }

        public Builder button(String label, String data, String color, KeyboardAction.Type type) {
            rowButtons.add(new InlineKeyboardButton(label, data, color, type));
            return this;
        }

        public Builder row() {
            buttons.add(Collections.unmodifiableList(new ArrayList<>(rowButtons)));
            rowButtons = new ArrayList<>();
            return this;
        }

        public InlineKeyboard build() {
            val buttons = new ArrayList<>(this.buttons);

            if (!rowButtons.isEmpty()) {
                buttons.add(Collections.unmodifiableList(new ArrayList<>(rowButtons)));
            }

            return new InlineKeyboard(Collections.unmodifiableList(buttons));
        }

    }

}
