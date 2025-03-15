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

import wbot.http.EmbeddableContent;
import wbot.platform.telegram.TelegramClient;
import wbot.platform.telegram.model.InputMedia;

/**
 * @author whilein
 */
public final class TelegramEditMessageMedia extends TelegramEdit<TelegramEditMessageMedia> {

    public TelegramEditMessageMedia(TelegramClient client) {
        super(client, "editMessageMedia", true);
    }

    public TelegramEditMessageMedia media(String type, String filename, EmbeddableContent attachment) {
        return media(type, filename, attachment, null);
    }


    public TelegramEditMessageMedia media(String type, String filename, EmbeddableContent attachment, String caption) {
        params.set("media", new InputMedia(type, "attach://1", caption));
        params.setFile("1", filename, attachment);
        return this;
    }

}
