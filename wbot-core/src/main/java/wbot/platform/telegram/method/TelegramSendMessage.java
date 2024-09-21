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

/**
 * @author whilein
 */
public final class TelegramSendMessage extends TelegramSend<TelegramSendMessage> {

    public TelegramSendMessage(TelegramClient client) {
        super(client, "sendMessage");
    }

    public TelegramSendMessage text(String text) {
        params.set("text", text);
        return this;
    }

    public TelegramSendMessage disableWebPageView(Boolean disableWebPageView) {
        params.set("disable_web_page_view", disableWebPageView);
        return this;
    }

}
