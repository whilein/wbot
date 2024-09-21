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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import wbot.platform.ApiMethod;
import wbot.platform.vk.VkClient;

import java.util.concurrent.CompletableFuture;

/**
 * @author whilein
 */
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class VkMethod<R> implements ApiMethod<R> {

    VkClient client;

    @Getter
    String name;

    @Getter
    Class<? extends R> type;

    @Getter
    VkMethodParams params;

    public VkMethod(VkClient client, String name, Class<? extends R> type) {
        this.client = client;
        this.name = name;
        this.type = type;
        this.params = new VkMethodParams(client.getJsonMapper(), client.getDefaultParams());
    }

    @Override
    public CompletableFuture<R> make() {
        return client.send(this);
    }


}
