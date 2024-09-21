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

package wbot.event;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.slf4j.Logger;
import wbot.model.InKeyboardCallback;
import wbot.model.InMessage;
import wbot.platform.Platform;

import java.util.Set;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class SimpleEventDispatcher implements EventDispatcher {

    Logger logger;

    Set<EventHandler> eventHandlers;

    @Override
    public void message(Platform platform, InMessage inMessage) {
        for (val eventHandler : eventHandlers)
            try {
                eventHandler.onMessage(platform, inMessage);
            } catch (Exception e) {
                logger.error("Failed to handle message by " + eventHandlers, e);
            }
    }

    @Override
    public void keyboardCallback(Platform platform, InKeyboardCallback inKeyboardCallback) {
        for (val eventHandler : eventHandlers)
            try {
                eventHandler.onKeyboardCallback(platform, inKeyboardCallback);
            } catch (Exception e) {
                logger.error("Failed to handle keyboard callback by " + eventHandlers, e);
            }
    }

}
