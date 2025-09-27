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

package wbot.platform.max;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import wbot.http.HttpMethodType;
import wbot.platform.ApiMethod;
import wbot.platform.max.method.MaxMethodParams;

import java.util.concurrent.CompletableFuture;

/**
 * @author _Novit_ (novitpw)
 */
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class MaxMethod<R> implements ApiMethod<R> {
    MaxClient client;

    @Getter
    String name;

    @Getter
    Class<? extends R> type;

    @Getter
    MaxMethodParams params;

    @Getter
    HttpMethodType methodType;

    public MaxMethod(
            MaxClient client,
            String name,
            Class<? extends R> type,
            HttpMethodType methodType
    ) {
        this.client = client;
        this.name = name;
        this.type = type;
        this.methodType = methodType;

        this.params = new MaxMethodParams(client.getJsonMapper(), client.getDefaultParams());
    }

    @Override
    public CompletableFuture<R> make() {
        return client.send(this);
    }

}
