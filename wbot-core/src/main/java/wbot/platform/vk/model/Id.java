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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import wbot.model.IdentityHolder;
import wbot.platform.PlatformType;

/**
 * @author whilein
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator(mode = JsonCreator.Mode.DELEGATING)))
public final class Id implements IdentityHolder {

    public static final long CHAT_OFFSET = 2000000000;

    @JsonValue long value;

    @Override
    public boolean isUser() {
        return !isChat() && !isBot();
    }

    @Override
    public PlatformType getPlatform() {
        return PlatformType.VK;
    }

    @Override
    public boolean isChat() {
        return value > CHAT_OFFSET;
    }

    @Override
    public boolean isBot() {
        return value < 0;
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj == this || obj instanceof Id && this.value == ((Id) obj).value;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }

}
