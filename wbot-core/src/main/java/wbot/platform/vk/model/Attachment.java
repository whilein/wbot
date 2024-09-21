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
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * @author _Novit_ (novitpw)
 */
@Value
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class Attachment {
    Type type;
    Document doc;
    Photo photo;

    public enum Type {
        @JsonProperty("audio") AUDIO,
        @JsonProperty("audio_message") AUDIO_MESSAGE,
        @JsonProperty("doc") DOC,
        @JsonProperty("event") EVENT,
        @JsonProperty("gift") GIFT,
        @JsonProperty("graffiti") GRAFFITI,
        @JsonProperty("link") LINK,
        @JsonProperty("market") MARKET,
        @JsonProperty("market_album") MARKET_ALBUM,
        @JsonProperty("page") PAGE,
        @JsonProperty("photo") PHOTO,
        @JsonProperty("poll") POLL,
        @JsonProperty("sticker") STICKER,
        @JsonProperty("story") STORY,
        @JsonProperty("wall") WALL_POST,
        @JsonProperty("wall_reply") WALL_REPLY,
        @JsonProperty("video") VIDEO
    }
}
