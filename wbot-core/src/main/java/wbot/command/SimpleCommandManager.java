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
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class SimpleCommandManager implements CommandManager {

    @Getter
    CommandKeyboardButtonPayloadCodec keyboardButtonPayloadCodec;

    Set<Command> commands = new HashSet<>();
    Map<String, Command> name2CommandMap = new HashMap<>();

    Map<String, CommandKeyboardButtonExecutor> name2KeyboardExecutorMap = new HashMap<>();

    @Override
    public @Nullable Command getCommand(@NotNull String commandName) {
        return name2CommandMap.get(commandName.toLowerCase());
    }

    @Override
    public @Nullable CommandKeyboardButtonExecutor getKeyboardButtonExecutor(@NotNull String name) {
        return name2KeyboardExecutorMap.get(name.toLowerCase());
    }

    @Override
    public boolean register(@NotNull Command command) {
        if (!commands.add(command)) return false;

        name2CommandMap.put(command.name().toLowerCase(), command);
        for (val alias : command.aliases()) {
            name2CommandMap.put(alias.toLowerCase(), command);
        }

        return true;
    }

    @Override
    public boolean unregister(@NotNull Command command) {
        if (!commands.remove(command)) return false;

        name2CommandMap.remove(command.name().toLowerCase(), command);
        for (val alias : command.aliases()) {
            name2CommandMap.remove(alias.toLowerCase(), command);
        }

        return true;
    }

    @Override
    public boolean unregister(String commandName) {
        val command = name2CommandMap.get(commandName.toLowerCase());

        return command != null && unregister(command);
    }

    @Override
    public @NotNull @Unmodifiable List<@NotNull Command> getCommands() {
        return Collections.unmodifiableList(new ArrayList<>(commands));
    }

    @Override
    public @NotNull CommandKeyboardButton registerKeyboardButton(
            @NotNull String name,
            @NotNull CommandKeyboardButtonExecutor executor
    ) {
        if (name2KeyboardExecutorMap.putIfAbsent(name, executor) != null) {
            throw new IllegalArgumentException("Keyboard button " + name + " already registered");
        }

        return new CommandKeyboardButton(keyboardButtonPayloadCodec, name);
    }
}
