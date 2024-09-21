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
public abstract class TelegramSend<S extends TelegramSend<S>> extends TelegramMethod<Message> {

    @SuppressWarnings("unchecked")
    protected final S self() {
        return (S) this;
    }

    protected TelegramSend(TelegramClient client, String name) {
        super(client, name, Message.class);
    }

    public TelegramSend(TelegramClient client, String name, boolean multipart) {
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

    public S replyToMessageId(long replyToMessageId) {
        params.set("reply_to_message_id", replyToMessageId);
        return self();
    }

    public S disableNotification(boolean disableNotification) {
        params.set("disable_notification", disableNotification);
        return self();
    }

    public S replyMarkup(ReplyMarkup replyMarkup) {
        params.set("reply_markup", replyMarkup);
        return self();
    }


}
