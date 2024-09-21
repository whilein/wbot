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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import wbot.platform.vk.VkClient;

/**
 * @author whilein
 */
public final class VkPhotosGetMessagesUploadServer extends VkMethod<VkPhotosGetMessagesUploadServer.Result> {

    public VkPhotosGetMessagesUploadServer(VkClient client) {
        super(client, "photos.getMessagesUploadServer", Result.class);
    }

    public VkPhotosGetMessagesUploadServer peerId(long peerId) {
        params.set("peer_id", peerId);
        return this;
    }

    @Value
    @Builder
    @Jacksonized
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        String uploadUrl;
        long albumId;
    }

}
