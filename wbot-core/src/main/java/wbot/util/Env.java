/*
 *    Copyright 2025 Whilein
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

package wbot.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author _Novit_ (novitpw)
 */
@UtilityClass
public class Env {

    @Contract("_, !null -> !null")
    public @Nullable String getString(@NotNull String key, @Nullable String defaultValue) {
        return findString(key).orElse(defaultValue);
    }

    public @NotNull Optional<@NotNull String> findString(@NotNull String key) {
        return Optional.ofNullable(System.getenv(key));
    }

}
