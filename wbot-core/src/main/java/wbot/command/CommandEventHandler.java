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
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import wbot.event.EventHandler;
import wbot.model.InKeyboardCallback;
import wbot.model.InMessage;
import wbot.platform.Platform;

import java.util.regex.Pattern;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class CommandEventHandler implements EventHandler {

    private static final Pattern SPACE = Pattern.compile(" +");

    CommandManager commandManager;

    @Override
    public void onKeyboardCallback(Platform platform, InKeyboardCallback inKeyboardCallback) {
        val payloadOpt = commandManager.getKeyboardButtonPayloadCodec().deserialize(inKeyboardCallback.getData());
        if (!payloadOpt.isPresent()) return;

        val payload = payloadOpt.get();
        val name = payload.getName();
        val executor = commandManager.getKeyboardButtonExecutor(name);
        if (executor == null) return;

        executor.execute(new KeyboardContext(platform, name, payload.getArgs(), inKeyboardCallback));
    }

    @Override
    public void onMessage(Platform platform, InMessage inMessage) {
        val text = inMessage.getText();
        if (text == null || text.isEmpty() || text.charAt(0) != '/') return;

        val params = SPACE.split(text.substring(1));

        val commandName = params[0];
        val command = commandManager.getCommand(commandName);
        if (command == null) return;

        command.execute(new CommandContext(platform, commandName, 1, params, inMessage));
    }

}
