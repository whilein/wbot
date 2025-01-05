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

package wbot.platform.telegram.method;

import wbot.platform.telegram.TelegramClient;
import wbot.platform.telegram.model.File;

/**
 * @author _Novit_ (novitpw)
 */
public final class TelegramGetFile extends TelegramMethod<File> {
    public TelegramGetFile(TelegramClient client) {
        super(client, "getFile", File.class);
    }

    public TelegramGetFile fileId(String fileId) {
        params.set("file_id", fileId);
        return this;
    }
}
