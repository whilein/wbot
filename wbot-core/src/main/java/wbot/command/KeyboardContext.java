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

package wbot.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbot.model.IdentityHolder;
import wbot.model.InKeyboardCallback;
import wbot.model.OutMessage;
import wbot.model.SentMessage;
import wbot.platform.Platform;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author whilein
 */
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class KeyboardContext implements ArgumentProvider {

    @Getter
    Platform platform;

    @Getter
    String name;

    Object[] arguments;

    @Getter
    InKeyboardCallback keyboardCallback;

    JsonMapper jsonMapper;

    public IdentityHolder chat() {
        return keyboardCallback.getChat();
    }

    public IdentityHolder from() {
        return keyboardCallback.getFrom();
    }

    @Override
    public String argument(int i) {
        return rawArgument(i)
                .map(Object::toString)
                .orElseThrow();
    }

    public @NotNull Optional<Object> rawArgument(int i) {
        return isValidIndex(i)
                ? Optional.ofNullable(arguments[i])
                : Optional.empty();
    }

    public <T> @NotNull Optional<T> argumentAs(int i, Class<T> type) {
        return rawArgument(i).flatMap(arg -> readValue(arg, type));
    }

    private <T> @NotNull Optional<T> readValue(Object value, Class<T> type) {
        try {
            return Optional.ofNullable(jsonMapper.readValue(value.toString(), type));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    private boolean isValidIndex(int i) {
        return i >= 0 && i < arguments.length;
    }

    @Override
    public int argumentCount() {
        return arguments.length;
    }

    public CompletableFuture<SentMessage> sendMessage(OutMessage message) {
        return platform.sendMessage(message);
    }

    public CompletableFuture<Void> editMessage(OutMessage message) {
        return platform.editMessage(keyboardCallback, message);
    }

    public CompletableFuture<Void> answerCallback(@Nullable String text) {
        return platform.answerCallback(keyboardCallback, text);
    }

}
