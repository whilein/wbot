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

package wbot.platform.max.method;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import wbot.http.BytesContent;
import wbot.http.Content;
import wbot.platform.ApiMethodParams;
import wbot.util.EncodeUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author _Novit_ (novitpw)
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class MaxMethodParams implements ApiMethodParams {
    JsonMapper jsonMapper;

    StringBuilder builder;

    public MaxMethodParams(JsonMapper jsonMapper, String prefix) {
        this.jsonMapper = jsonMapper;
        this.builder = new StringBuilder(prefix);
    }

    @SneakyThrows
    public void setJson(String field, Object value) {
        set(field, jsonMapper.writeValueAsString(value));
    }

    public void set(String field, String[] value) {
        appendField(field);

        for (int i = 0, j = value.length; i < j; i++) {
            if (i != 0) {
                builder.append(',');
            }

            builder.append(EncodeUtils.encodeURL(value[i]));
        }
    }

    public void set(String field, String value) {
        appendField(field).append(EncodeUtils.encodeURL(value));
    }

    public void set(String field, Number value) {
        appendField(field).append(value);
    }

    public void set(String field, long[] value) {
        appendField(field);

        for (int i = 0, j = value.length; i < j; i++) {
            if (i != 0) {
                builder.append(',');
            }

            builder.append(value[i]);
        }
    }

    public void set(String field, int[] value) {
        appendField(field);

        for (int i = 0, j = value.length; i < j; i++) {
            if (i != 0) {
                builder.append(',');
            }

            builder.append(value[i]);
        }
    }

    public void set(String field, long value) {
        appendField(field).append(value);
    }

    public void set(String field, boolean value) {
        set(field, value ? 1 : 0);
    }

    public void set(String field, int value) {
        appendField(field).append(value);
    }

    private StringBuilder appendField(String field) {
        return builder.append('&').append(field).append('=');
    }

    @Override
    public Content asContent() {
        return new BytesContent("application/x-www-form-urlencoded", builder.toString(),
                StandardCharsets.UTF_8);
    }
}
