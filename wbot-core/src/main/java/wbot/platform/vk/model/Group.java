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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * @author _Novit_ (novitpw)
 */
@Value
@Builder
@Jacksonized
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Group {
    int id;
    String name;
    String screenName;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    boolean isClosed;
    String deactivated;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    boolean isAdmin;
    int adminLevel;
    String type;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    boolean hasPhoto;
    @JsonProperty("photo_100")
    String photo100;
    @JsonProperty("photo_200")
    String photo200;
    @JsonProperty("photo_400")
    String photo400;
    String activity;
    String description;
    int fixedPost;
    int membersCount;
    String site;
    long startDate;
    long finishDate;
    String status;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    boolean verified;
}
