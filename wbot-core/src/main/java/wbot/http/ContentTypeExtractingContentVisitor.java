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
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ContentTypeExtractingContentVisitor implements ContentVisitor<String> {

    private static final ContentVisitor<String> INSTANCE = new ContentTypeExtractingContentVisitor();

    public static ContentVisitor<String> getInstance() {
        return INSTANCE;
    }

    public static String getMultipartType(String boundary) {
        return "multipart/form-data; boundary=" + boundary;
    }

    @Override
    public String visitInputStream(String contentType, long size, InputStream is) {
        return contentType;
    }

    @Override
    public String visitMultipart(String contentType, String boundary, List<MultipartContent.Part> parts) {
        return contentType;
    }

    @Override
    public String visitFile(String contentType, long size, Path path) {
        return contentType;
    }

    @Override
    public String visitBytes(String contentType, byte[] bytes, int off, int len) {
        return contentType;
    }

}
