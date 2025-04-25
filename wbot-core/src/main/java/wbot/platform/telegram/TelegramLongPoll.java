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

package wbot.platform.telegram;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.slf4j.Logger;
import wbot.platform.telegram.model.Update;

import java.util.function.Consumer;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class TelegramLongPoll {

    private static final int TIMEOUT = 90;

    Logger logger;

    TelegramClient telegramClient;

    public void start(Consumer<Update> updateHandler) {
        Integer offset = null;

        while (true) {
            try {
                val updates = telegramClient.getUpdates()
                        .timeout(TIMEOUT)
                        .offset(offset)
                        .make()
                        .get();

                logger.debug("Received {} updates", updates.length);

                for (val update : updates) {
                    try {
                        updateHandler.accept(update);
                    } catch (Exception e) {
                        logger.error("Cannot handle update", e);
                    }

                    offset = update.getUpdateId() + 1;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.error("LongPoll receiving updates failure", e);
            }
        }
    }

}
