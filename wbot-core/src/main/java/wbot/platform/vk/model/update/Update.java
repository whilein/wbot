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

package wbot.platform.vk.model.update;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * @author whilein
 */
@Value
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class Update {
    String type;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type",
            defaultImpl = UpdateStub.class
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = MessageNew.class, name = "message_new"),
            @JsonSubTypes.Type(value = MessageEvent.class, name = "message_event"),
            @JsonSubTypes.Type(value = MessageAllow.class, name = "message_allow"),
            @JsonSubTypes.Type(value = MessageDeny.class, name = "message_deny"),
            @JsonSubTypes.Type(value = MessageEdit.class, name = "message_edit"),
            @JsonSubTypes.Type(value = MessageReply.class, name = "message_reply")
    })
    UpdateObject object;
}
