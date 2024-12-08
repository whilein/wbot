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

package wbot.platform.vk.method;

import wbot.platform.vk.VkClient;
import wbot.platform.vk.model.Id;
import wbot.platform.vk.model.Keyboard;

/**
 * @author whilein
 */
public final class VkMessagesEdit extends VkMethod<Integer> {

    public VkMessagesEdit(
            VkClient vkClient
    ) {
        super(vkClient, "messages.edit", Integer.class);
    }

    public VkMessagesEdit peerId(Id peerId) {
        return peerId(peerId.getValue());
    }

    public VkMessagesEdit peerId(long peerId) {
        params.set("peer_id", peerId);
        return this;
    }
    
    public VkMessagesEdit messageId(long messageId) {
        params.set("message_id", messageId);
        return this;
    }

    public VkMessagesEdit conversationMessageId(long conversationMessageId) {
        params.set("conversation_message_id", conversationMessageId);
        return this;
    }
    
    public VkMessagesEdit message(String message) {
        params.set("message", message);
        return this;
    }

    public VkMessagesEdit attachment(String attachment) {
        params.set("attachment", attachment);
        return this;
    }
    
    public VkMessagesEdit keyboard(Keyboard keyboard) {
        params.setJson("keyboard", keyboard);
        return this;
    }

    public VkMessagesEdit dontParseLinks(boolean dontParseLinks) {
        params.set("dont_parse_links", dontParseLinks);
        return this;
    }

    public VkMessagesEdit keepForwardMessages(boolean keepForwardMessages) {
        params.set("keep_forward_messages", keepForwardMessages);
        return this;
    }

    public VkMessagesEdit disableMentions(boolean disableMentions) {
        params.set("disable_mentions", disableMentions);
        return this;
    }

    public VkMessagesEdit latitude(Float latitude) {
        params.set("lat", latitude);
        return this;
    }

    public VkMessagesEdit longitude(Float longitude) {
        params.set("long", longitude);
        return this;
    }

}
