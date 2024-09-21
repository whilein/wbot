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
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.util.ArrayList;
import java.util.List;

/**
 * @author whilein
 */
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class MultipartContent implements Content {

    @Getter
    String contentType;

    @Getter
    String boundary;

    List<Part> parts;

    public MultipartContent(String boundary, List<Part> parts) {
        this.contentType = "multipart/form-data; boundary=" + boundary;
        this.boundary = boundary;
        this.parts = new ArrayList<>(parts);
    }

    private static String makeBoundary() {
        // "adapted" from Jetty
        StringBuilder builder = new StringBuilder("WBOT");
        builder.append(Long.toString(System.identityHashCode(builder), 36));
        builder.append(Long.toString(System.identityHashCode(Thread.currentThread()), 36));
        builder.append(Long.toString(System.nanoTime(), 36));
        return builder.toString();
    }

    @Override
    public <R> R accept(ContentVisitor<R> cv) {
        return cv.visitMultipart(contentType, boundary, parts);
    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor
    public static final class Part {
        String name;
        String fileName;
        EmbeddableContent content;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static final class Builder {

        @Setter
        @NonFinal
        @Accessors(fluent = true)
        String boundary;

        List<Part> parts = new ArrayList<>();

        public Builder addPart(Part part) {
            this.parts.add(part);
            return this;
        }

        public MultipartContent build() {
            String boundary;
            if ((boundary = this.boundary) == null) {
                boundary = makeBoundary();
            }

            return new MultipartContent(boundary, parts);
        }

    }

}
