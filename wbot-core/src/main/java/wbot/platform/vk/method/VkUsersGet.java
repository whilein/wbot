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
import wbot.platform.vk.model.User;
import wbot.platform.vk.model.UserNameCase;

/**
 * @author whilein
 */
public final class VkUsersGet extends VkMethod<User[]> {

    public VkUsersGet(VkClient client) {
        super(client, "users.get", User[].class);
    }

    public VkUsersGet nameCase(UserNameCase nameCase) {
        params.set("name_case", nameCase.name());
        return this;
    }

    public VkUsersGet userIds(long... userIds) {
        params.set("user_ids", userIds);
        return this;
    }

    public VkUsersGet fields(String... fields) {
        params.set("fields", fields);
        return this;
    }

}
