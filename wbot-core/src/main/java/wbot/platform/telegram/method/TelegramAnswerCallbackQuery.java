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
 * @author _Novit_ (novitpw)
 */
public class TelegramAnswerCallbackQuery extends TelegramMethod<Boolean> {

    public TelegramAnswerCallbackQuery(TelegramClient client) {
        super(client, "answerCallbackQuery", Boolean.class);
    }

    public TelegramAnswerCallbackQuery queryId(String queryId) {
        params.set("callback_query_id", queryId);
        return this;
    }

    public TelegramAnswerCallbackQuery text(String text) {
            params.set("text", text);
            return this;
    }

    public TelegramAnswerCallbackQuery showAlert(boolean showAlert) {
            params.set("show_alert", showAlert);
            return this;
    }

    public TelegramAnswerCallbackQuery url(String url) {
        params.set("url", url);
        return this;
    }

    public TelegramAnswerCallbackQuery cacheTime(Integer cacheTime) {
        params.set("cache_time", cacheTime);
        return this;
    }
}
