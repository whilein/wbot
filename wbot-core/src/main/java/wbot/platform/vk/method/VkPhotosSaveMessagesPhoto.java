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
import wbot.platform.vk.model.Photo;

/**
 * @author whilein
 */
public final class VkPhotosSaveMessagesPhoto extends VkMethod<Photo[]> {

    public VkPhotosSaveMessagesPhoto(VkClient client) {
        super(client, "photos.saveMessagesPhoto", Photo[].class);
    }

    public VkPhotosSaveMessagesPhoto hash(String hash) {
        params.set("hash", hash);
        return this;
    }

    public VkPhotosSaveMessagesPhoto photo(String photo) {
        params.set("photo", photo);
        return this;
    }

    public VkPhotosSaveMessagesPhoto server(int server) {
        params.set("server", server);
        return this;
    }

}
