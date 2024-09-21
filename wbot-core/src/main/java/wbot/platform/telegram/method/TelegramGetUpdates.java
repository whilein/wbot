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

package wbot.platform.telegram.method;

import wbot.platform.telegram.TelegramClient;
import wbot.platform.telegram.model.Update;

import java.util.List;

/**
 * @author whilein
 */
public final class TelegramGetUpdates extends TelegramMethod<Update[]> {

    public TelegramGetUpdates(TelegramClient client) {
        super(client, "getUpdates", Update[].class);
    }

    public TelegramGetUpdates offset(Integer offset) {
        params.set("offset", offset);
        return this;
    }

    public TelegramGetUpdates limit(Integer limit) {
        params.set("limit", limit);
        return this;
    }

    public TelegramGetUpdates timeout(Integer timeout) {
        params.set("timeout", timeout);
        return this;
    }

    public TelegramGetUpdates allowedUpdates(List<String> allowedUpdates) {
        params.set("allowedUpdates", allowedUpdates);
        return this;
    }

}
