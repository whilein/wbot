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

package wbot.http;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.ByteArrayInputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

/**
 * @author whilein
 */
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class BytesContent implements EmbeddableContent {
    @Getter
    String contentType;

    byte[] bytes;
    int off;
    int len;

    public BytesContent(String contentType, String string, Charset charset) {
        this(contentType, string.getBytes(charset));
    }

    public BytesContent(String contentType, byte[] bytes) {
        this(contentType, bytes, 0, bytes.length);
    }

    @Override
    public <R> R accept(ContentVisitor<R> cv) {
        return cv.visitBytes(contentType, bytes, off, len);
    }

    @Override
    public ReadableByteChannel open() {
        return Channels.newChannel(new ByteArrayInputStream(bytes, off, len));
    }

}
