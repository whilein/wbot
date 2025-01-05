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

package wbot.platform.vk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

/**
 * @author whilein
 */
@Value
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class User {
    long id;
    String firstName;
    String lastName;
    String deactivated;
    boolean isClosed;
    boolean canAccessClosed;
    @Accessors(fluent = true)
    boolean hasPhoto;
    @JsonProperty("photo_50")
    String photo50;
    @JsonProperty("photo_100")
    String photo100;
    @JsonProperty("photo_200_orig")
    String photo200Orig;
    @JsonProperty("photo_200")
    String photo200;
    @JsonProperty("photo_400")
    String photo400;
    String photoMax;
    String photoMaxOrig;
}
