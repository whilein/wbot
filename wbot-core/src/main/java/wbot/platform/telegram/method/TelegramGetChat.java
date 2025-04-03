package wbot.platform.telegram.method;

import wbot.platform.telegram.TelegramClient;
import wbot.platform.telegram.model.ChatFullInfo;

/**
 * @author _Novit_ (novitpw)
 */
public final class TelegramGetChat extends TelegramMethod<ChatFullInfo> {
    public TelegramGetChat(TelegramClient client) {
        super(client, "getChat", ChatFullInfo.class);
    }

    public TelegramGetChat setChatId(String chatId) {
        params.set("chat_id", chatId);
        return this;
    }

    public TelegramGetChat setChatId(Long chatId) {
        params.set("chat_id", chatId);
        return this;
    }

}
