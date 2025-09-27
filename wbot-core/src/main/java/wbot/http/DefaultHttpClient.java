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
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class DefaultHttpClient implements HttpClient {

    private static final ContentVisitor<String> CONTENT_TYPE = ContentTypeExtractingContentVisitor.getInstance();
    private static final byte[] CRLF = "\r\n".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] BOUNDARY_DELIMITER = "--".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] PART_CONTENT_DISPOSITION_START = "Content-Disposition: form-data; name=\""
            .getBytes(StandardCharsets.US_ASCII);

    private static final byte[] PART_CONTENT_DISPOSITION_FILENAME = "\"; filename=\""
            .getBytes(StandardCharsets.US_ASCII);

    private static final byte[] PART_CONTENT_DISPOSITION_END = "\"\r\n".getBytes(StandardCharsets.US_ASCII);

    private static final byte[] PART_CONTENT_TYPE = "Content-Type: ".getBytes(StandardCharsets.US_ASCII);

    int nThreads;

    @NonFinal
    volatile ExecutorService executor;

    @Override
    public void start() {
        executor = nThreads > 0
                ? Executors.newFixedThreadPool(nThreads)
                : Executors.newCachedThreadPool();
    }

    @Override
    public void stop() {
        executor.shutdown();
        executor = null;
    }

    @Override
    public CompletableFuture<HttpResponse> get(String url) {
        return dataRetrievalRequest(url, HttpMethodType.GET);
    }

    @Override
    public CompletableFuture<HttpResponse> delete(String url) {
        return dataRetrievalRequest(url, HttpMethodType.DELETE);
    }

    private CompletableFuture<HttpResponse> dataRetrievalRequest(
            String url,
            HttpMethodType methodType
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                val urlConnection = (HttpURLConnection) new URL(url).openConnection();
                urlConnection.setRequestMethod(methodType.name());
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(false);

                return getResponse(urlConnection);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<HttpResponse> post(String url, Content content) {
        return resourceChangeRequest(url, content, HttpMethodType.POST);
    }

    @Override
    public CompletableFuture<HttpResponse> put(String url, Content content) {
        return resourceChangeRequest(url, content, HttpMethodType.PUT);
    }

    @Override
    public CompletableFuture<HttpResponse> patch(String url, Content content) {
        return resourceChangeRequest(url, content, HttpMethodType.PATCH);
    }

    private CompletableFuture<HttpResponse> resourceChangeRequest(
            String url,
            Content content,
            HttpMethodType methodType
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                val urlConnection = (HttpURLConnection) new URL(url).openConnection();
                urlConnection.setRequestMethod(methodType.name());
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                content.accept(new RequestBootstrappingContentVisitor(urlConnection));

                return getResponse(urlConnection);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    private HttpResponse getResponse(HttpURLConnection connection) throws IOException {
        connection.getResponseCode();

        InputStream stream = connection.getErrorStream();
        if (stream == null) {
            stream = connection.getInputStream();
        }

        return new HttpResponse(connection.getResponseCode(), stream);
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor
    private static final class RequestSenderContentVisitor extends ExceptionSneakyPropagatingContentVisitor<Void> {

        OutputStream output;

        @NonFinal
        byte[] buf;

        private byte[] touchBuffer() {
            byte[] buf;
            if ((buf = this.buf) == null) {
                this.buf = buf = new byte[8192];
            }
            return buf;
        }

        private static String getContentType(MultipartContent.Part part) {
            return part.content().accept(CONTENT_TYPE);
        }

        @Override
        public Void visitMultipart0(String contentType, String boundary, List<MultipartContent.Part> parts) throws IOException {
            val boundaryBytes = boundary.getBytes(StandardCharsets.US_ASCII);

            val os = output;
            os.write(BOUNDARY_DELIMITER);
            os.write(boundaryBytes);

            if (!parts.isEmpty()) {
                val iterator = parts.iterator();

                do {
                    os.write(CRLF);

                    val part = iterator.next();
                    os.write(PART_CONTENT_DISPOSITION_START);
                    os.write(part.name().getBytes(StandardCharsets.UTF_8));

                    String filename;
                    if ((filename = part.fileName()) != null) {
                        os.write(PART_CONTENT_DISPOSITION_FILENAME);
                        os.write(filename.getBytes(StandardCharsets.UTF_8));
                    }
                    os.write(PART_CONTENT_DISPOSITION_END);

                    String partContentType;
                    if ((partContentType = getContentType(part)) != null) {
                        os.write(PART_CONTENT_TYPE);
                        os.write(partContentType.getBytes(StandardCharsets.UTF_8));
                        os.write(CRLF);
                    }
                    os.write(CRLF);

                    part.content().accept(this);

                    os.write(CRLF);
                    os.write(BOUNDARY_DELIMITER);
                    os.write(boundaryBytes);
                } while (iterator.hasNext());
            }
            os.write(BOUNDARY_DELIMITER);
            os.write(CRLF);

            return null;
        }

        @Override
        public Void visitInputStream0(String contentType, long size, InputStream is) throws IOException {
            val buf = touchBuffer();
            int n;
            while ((n = is.read(buf)) != -1) {
                output.write(buf, 0, n);
            }
            return null;
        }

        @Override
        public Void visitFile0(String contentType, long size, Path path) throws IOException {
            try (val is = Files.newInputStream(path)) {
                return visitInputStream0(contentType, size, is);
            }
        }

        @Override
        public Void visitBytes0(String contentType, byte[] bytes, int off, int len) throws IOException {
            output.write(bytes, off, len);
            return null;
        }

    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor
    private static final class RequestBootstrappingContentVisitor
            extends ExceptionSneakyPropagatingContentVisitor<Void> {

        HttpURLConnection connection;

        private OutputStream configure(String contentType, long contentLength) throws IOException {
            connection.setRequestProperty("Content-Type", contentType);

            if (contentLength > 0L) {
                connection.setFixedLengthStreamingMode(contentLength);
            } else {
                connection.setChunkedStreamingMode(0);
            }

            return connection.getOutputStream();
        }

        @Override
        public Void visitInputStream0(String contentType, long size, InputStream is) throws IOException {
            try (val output = configure(contentType, size)) {
                return new RequestSenderContentVisitor(output)
                        .visitInputStream(contentType, size, is);
            }
        }

        @Override
        public Void visitMultipart0(String contentType, String boundary, List<MultipartContent.Part> parts) throws IOException {
            try (val output = configure(contentType, -1L)) {
                return new RequestSenderContentVisitor(output)
                        .visitMultipart0(contentType, boundary, parts);
            }
        }

        @Override
        public Void visitFile0(String contentType, long size, Path path) throws IOException {
            try (val output = configure(contentType, size)) {
                return new RequestSenderContentVisitor(output)
                        .visitFile(contentType, size, path);
            }
        }

        @Override
        public Void visitBytes0(String contentType, byte[] bytes, int off, int len) throws IOException {
            try (val output = configure(contentType, len)) {
                return new RequestSenderContentVisitor(output)
                        .visitBytes(contentType, bytes, off, len);
            }
        }

    }

}
