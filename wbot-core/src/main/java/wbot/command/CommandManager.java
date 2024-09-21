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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

/**
 * @author whilein
 */
@NotThreadSafe
public interface CommandManager {

    @NotNull CommandKeyboardButtonPayloadCodec getKeyboardButtonPayloadCodec();

    /**
     * Retrieves a command by its name or alias (case-insensitive).
     *
     * @param commandName The name or alias of the command to retrieve.
     * @return The found command if it exists; otherwise, returns {@code null}.
     */
    @Nullable Command getCommand(@NotNull String commandName);

    @Nullable CommandKeyboardButtonExecutor getKeyboardButtonExecutor(@NotNull String name);

    @NotNull CommandKeyboardButton registerKeyboardButton(
            @NotNull String name,
            @NotNull CommandKeyboardButtonExecutor executor
    );

    /**
     * Register the command
     *
     * @param command The command to register
     * @return {@code true} if the command was successfully registered; {@code false} otherwise.
     */
    boolean register(@NotNull Command command);

    /**
     * Unregisters a previously registered command.
     *
     * @param command The command to unregister.
     * @return {@code true} if the command was successfully unregistered; {@code false} otherwise.
     */
    boolean unregister(@NotNull Command command);

    /**
     * Unregisters a command by its name or alias (case-insensitive).
     *
     * @param commandName The name or alias of the command to unregister.
     * @return {@code true} if the command was successfully unregistered; {@code false} otherwise.
     */
    boolean unregister(@NotNull String commandName);

    /**
     * Retrieves a list of all registered commands.
     *
     * @return A list containing all registered commands.
     */
    @Unmodifiable @NotNull List<@NotNull Command> getCommands();

}
