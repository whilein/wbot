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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import wbot.model.IdentityHolder;
import wbot.model.InMessage;
import wbot.model.OutMessage;
import wbot.model.SentMessage;
import wbot.platform.Platform;

import java.util.concurrent.CompletableFuture;

/**
 * @author whilein
 */
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class CommandContext implements ArgumentProvider {

    @Getter
    Platform platform;

    @Getter
    String name;

    int argumentOffset;

    String[] arguments;

    @Getter
    InMessage message;

    public IdentityHolder chat() {
        return message.getChat();
    }

    public IdentityHolder from() {
        return message.getFrom();
    }

    @Override
    public String argument(int i) {
        return arguments[i + argumentOffset];
    }

    @Override
    public int argumentCount() {
        return arguments.length - argumentOffset;
    }

    public CompletableFuture<SentMessage> sendMessage(OutMessage message) {
        return platform.sendMessage(message);
    }

}
