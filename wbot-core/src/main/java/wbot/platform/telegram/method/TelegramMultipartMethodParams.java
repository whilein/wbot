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
import wbot.http.MultipartContent;

import java.nio.charset.StandardCharsets;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class TelegramMultipartMethodParams implements TelegramMethodParams {

    JsonMapper jsonMapper;

    MultipartContent.Builder multiPartContent = new MultipartContent.Builder();

    @Override
    @SneakyThrows
    public void set(String field, Object value) {
        if (value == null) {
            return;
        }

        multiPartContent.addPart(new MultipartContent.Part(field, null,
                !(value instanceof String) && !(value instanceof Number)
                ? new BytesContent(null, jsonMapper.writeValueAsBytes(value))
                : new BytesContent(null, value.toString(), StandardCharsets.UTF_8)));
    }

    @Override
    public void setFile(String field, String filename, EmbeddableContent content) {
        multiPartContent.addPart(new MultipartContent.Part(field, filename, content));
    }

    @SneakyThrows
    public Content asContent() {
        return multiPartContent.build();
    }

}
