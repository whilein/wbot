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

package wbot.http.java11;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import wbot.http.Content;
import wbot.http.ExceptionSneakyPropagatingContentVisitor;
import wbot.http.HttpClient;
import wbot.http.HttpResponse;
import wbot.http.MultipartContent;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class Java11HttpClient implements HttpClient {

    java.net.http.HttpClient httpClient;

    @Override
    public void start() {
        // no-op
    }

    @Override
    public void stop() {
        // no-op
    }

    @Override
    public CompletableFuture<HttpResponse> post(String url, Content content) {
        val request = content.accept(new RequestInitializingContentVisitor(url));

        return httpClient.sendAsync(request, BodyHandlers.ofInputStream())
                .thenApply(response -> new HttpResponse(response.body()));
    }

    @Override
    public CompletableFuture<HttpResponse> get(String url) {
        return httpClient.sendAsync(HttpRequest.newBuilder(URI.create(url)).build(), BodyHandlers.ofInputStream())
                .thenApply(response -> new HttpResponse(response.body()));
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor
    private static final class RequestInitializingContentVisitor extends ExceptionSneakyPropagatingContentVisitor<HttpRequest> {

        String url;

        private HttpRequest.Builder sendRequest(HttpRequest.BodyPublisher content) {
            return HttpRequest.newBuilder(URI.create(url)).POST(content);
        }

        @Override
        public HttpRequest visitInputStream0(String contentType, long size, InputStream is) {
            return sendRequest(HttpRequest.BodyPublishers.ofInputStream(() -> is))
                    .setHeader("Content-Type", contentType)
                    .build();
        }

        @Override
        public HttpRequest visitMultipart0(String contentType, String boundary, List<MultipartContent.Part> parts) {
            return sendRequest(HttpRequest.BodyPublishers.fromPublisher(new MultipartFormDataBodyPublisher(boundary, parts)))
                    .setHeader("Content-Type", contentType)
                    .build();
        }

        @Override
        public HttpRequest visitFile0(String contentType, long size, Path path) throws FileNotFoundException {
            return sendRequest(HttpRequest.BodyPublishers.ofFile(path))
                    .setHeader("Content-Type", contentType)
                    .build();
        }

        @Override
        public HttpRequest visitBytes0(String contentType, byte[] bytes, int off, int len) {
            return sendRequest(HttpRequest.BodyPublishers.ofByteArray(bytes, off, len))
                    .setHeader("Content-Type", contentType)
                    .build();
        }
    }
}
