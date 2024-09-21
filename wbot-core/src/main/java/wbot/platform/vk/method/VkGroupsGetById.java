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
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import wbot.platform.vk.VkClient;
import wbot.platform.vk.model.Group;

/**
 * @author _Novit_ (novitpw)
 */
public final class VkGroupsGetById extends VkMethod<VkGroupsGetById.Result> {

    public VkGroupsGetById(VkClient client) {
        super(client, "groups.getById", Result.class);
    }

    public VkGroupsGetById groupId(long groupId) {
        params.set("group_id", groupId);
        return this;
    }

    public VkGroupsGetById groupIds(long... groupIds) {
        params.set("group_ids", groupIds);
        return this;
    }

    public VkGroupsGetById fields(String... fields) {
        params.set("fields", fields);
        return this;
    }

    @Value
    @Builder
    @Jacksonized
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        Group[] groups;
        // todo profiles
    }
}
