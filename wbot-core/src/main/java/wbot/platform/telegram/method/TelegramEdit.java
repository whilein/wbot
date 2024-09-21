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
import wbot.platform.telegram.model.Message;
import wbot.platform.telegram.model.ReplyMarkup;

/**
 * @author whilein
 */
public abstract class TelegramEdit<S extends TelegramEdit<S>> extends TelegramMethod<Message> {

    @SuppressWarnings("unchecked")
    protected final S self() {
        return (S) this;
    }

    protected TelegramEdit(TelegramClient client, String name) {
        super(client, name, Message.class);
    }

    public TelegramEdit(TelegramClient client, String name, boolean multipart) {
        super(client, name, Message.class, multipart);
    }

    public S chatId(long chatId) {
        params.set("chat_id", chatId);
        return self();
    }

    public S parseMode(String parseMode) {
        params.set("parse_mode", parseMode);
        return self();
    }

    public S messageId(long messageId) {
        params.set("message_id", messageId);
        return self();
    }

    public S replyMarkup(ReplyMarkup replyMarkup) {
        params.set("reply_markup", replyMarkup);
        return self();
    }


}
