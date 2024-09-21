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

package wbot.model;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * @author whilein
 */
@Value
public class OutMessage {
    Identity chat;
    Long reply;
    String text;
    InlineKeyboard keyboard;
    Attachment attachment;

    public boolean hasAttachment() {
        return attachment != null;
    }

    @Accessors(fluent = true)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static final class Builder {

        Identity chat;
        Long reply;

        @Setter
        String text;

        @Setter
        InlineKeyboard keyboard;

        @Setter
        Attachment attachment;

        public Builder reply(Identity chat, Long messageId) {
            this.chat = chat;
            this.reply = messageId;
            return this;
        }

        public Builder reply(InMessage inMessage) {
            this.chat = inMessage.getChat().getIdentity();
            this.reply = inMessage.getId();
            return this;
        }

        public Builder chat(InMessage inMessage) {
            return chat(inMessage.getChat());
        }

        public Builder chat(Identity chat) {
            this.chat = chat;
            this.reply = null;

            return this;
        }

        public Builder chat(IdentityHolder chat) {
            return chat(chat.getIdentity());
        }

        public OutMessage build() {
            if (chat == null) {
                throw new IllegalStateException("Chat is required");
            }

            if (text == null) {
                throw new IllegalStateException("Text is required");
            }

            return new OutMessage(chat, reply, text, keyboard, attachment);
        }


    }
}
