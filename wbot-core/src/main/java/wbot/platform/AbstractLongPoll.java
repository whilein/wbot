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

package wbot.platform;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * @author _Novit_ (novitpw)
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractLongPoll<T> {

    Logger logger;

    private static final long MAX_BACKOFF = 10000;
    private static final long MIN_BACKOFF = 100;

    private static final double BACKOFF_FACTOR = 3;

    @SuppressWarnings("BusyWait")
    public final void start(Consumer<T> updateHandler) throws Exception {
        onStart();

        long delayMs = MIN_BACKOFF;

        while (true) {
            try {
                poll(updateHandler);
                delayMs = MIN_BACKOFF;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.error("LongPoll receiving updates failure", e);
                logger.info("Retrying connection attempt in {}s", delayMs / 1000.0);

                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }

                delayMs = Math.min((long) (delayMs * BACKOFF_FACTOR), MAX_BACKOFF);
            }
        }
    }

    protected void onStart() throws Exception {
    }

    protected abstract void poll(Consumer<T> updateHandler) throws InterruptedException, ExecutionException;

}
