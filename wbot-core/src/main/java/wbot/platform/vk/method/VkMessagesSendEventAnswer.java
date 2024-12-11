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

/**
 * @author _Novit_ (novitpw)
 */
public final class VkMessagesSendEventAnswer extends VkMethod<Integer> {

    public VkMessagesSendEventAnswer(VkClient vkClient) {
        super(vkClient, "messages.sendMessageEventAnswer", Integer.class);
    }

    public VkMessagesSendEventAnswer peerId(Id peerId) {
        return peerId(peerId.getValue());
    }

    public VkMessagesSendEventAnswer peerId(long peerId) {
        params.set("peer_id", peerId);
        return this;
    }
    
    public VkMessagesSendEventAnswer eventId(String eventId) {
        params.set("event_id", eventId);
        return this;
    }

    public VkMessagesSendEventAnswer userId(Id userId) {
        return userId(userId.getValue());
    }

    public VkMessagesSendEventAnswer userId(long userId) {
        params.set("user_id", userId);
        return this;
    }
    
    public VkMessagesSendEventAnswer data(String data) {
        params.set("event_data", data);
        return this;
    }

}
