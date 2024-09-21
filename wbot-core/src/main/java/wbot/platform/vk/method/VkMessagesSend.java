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
import wbot.platform.vk.model.Forward;
import wbot.platform.vk.model.Id;
import wbot.platform.vk.model.Keyboard;

/**
 * @author whilein, _Novit_ (novitpw)
 */
public final class VkMessagesSend extends VkMethod<Long> {

    public VkMessagesSend(
            VkClient vkClient
    ) {
        super(vkClient, "messages.send", Long.class);

        params.set("random_id", System.nanoTime());
    }

    public VkMessagesSend peerId(Id peerId) {
        return peerId(peerId.getValue());
    }

    public VkMessagesSend peerId(long peerId) {
        params.set("peer_id", peerId);
        return this;
    }

    public VkMessagesSend message(String message) {
        params.set("message", message);
        return this;
    }

    public VkMessagesSend forwardMessages(int[] forwardMessages) {
        params.set("forward_messages", forwardMessages);
        return this;
    }

    public VkMessagesSend replyTo(long replyTo) {
        params.set("reply_to", replyTo);
        return this;
    }

    public VkMessagesSend attachment(String attachment) {
        params.set("attachment", attachment);
        return this;
    }

    public VkMessagesSend forward(Forward forward) {
        params.setJson("forward", forward);
        return this;
    }

    public VkMessagesSend keyboard(Keyboard keyboard) {
        params.setJson("keyboard", keyboard);
        return this;
    }

    public VkMessagesSend dontParseLinks(boolean dontParseLinks) {
        params.set("dont_parse_links", dontParseLinks);
        return this;
    }

    public VkMessagesSend latitude(Float latitude) {
        params.set("lat", latitude);
        return this;
    }

    public VkMessagesSend longitude(Float longitude) {
        params.set("long", longitude);
        return this;
    }

}
