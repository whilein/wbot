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

package wbot.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class SimpleCommandKeyboardButtonPayloadCodec implements CommandKeyboardButtonPayloadCodec {
    JsonMapper jsonMapper;

    @Override
    public @NotNull String serialize(@NotNull CommandKeyboardButtonPayload payload) {
        try {
            return jsonMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull Optional<@NotNull CommandKeyboardButtonPayload> deserialize(@NotNull String payload) {
        try {
            return Optional.of(jsonMapper.readValue(payload, CommandKeyboardButtonPayload.class));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }
}
