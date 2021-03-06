/*
 *    Copyright 2022 Whilein
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

package w.bot.longpoll.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import w.bot.VkBot;
import w.bot.id.Id;
import w.bot.type.Message;

/**
 * @author whilein
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class VkChatUserEvent extends VkMessageNewEvent {

    Id memberId;
    String email;

    public VkChatUserEvent(final VkBot bot, final Message message,
                           final Id memberId, final String email) {
        super(bot, message);

        this.memberId = memberId;
        this.email = email;
    }

    public boolean isEmail() {
        return memberId.isGroup() && email != null;
    }

}
