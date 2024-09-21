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

package wbot.platform.telegram.method;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import wbot.http.BytesContent;
import wbot.http.Content;
import wbot.http.EmbeddableContent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class TelegramJsonMethodParams implements TelegramMethodParams {

    JsonMapper jsonMapper;

    Map<String, Object> params = new HashMap<>();

    @Override
    public void set(String field, Object value) {
        if (value == null) {
            return;
        }

        params.put(field, value);
    }

    @Override
    public void setFile(String field, String filename, EmbeddableContent fileContent) {
        throw new UnsupportedOperationException();
    }

    @SneakyThrows
    public Content asContent() {
        return new BytesContent(
                "application/json",
                jsonMapper.writeValueAsBytes(params)
        );
    }

}
