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
import lombok.experimental.FieldDefaults;
import lombok.val;
import wbot.http.MultipartContent;

import java.io.IOException;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Flow.Subscriber;

/**
 * multipart/form-data BodyPublisher.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MultipartFormDataBodyPublisher implements BodyPublisher {

    BodyPublisher delegate;

    public MultipartFormDataBodyPublisher(String boundary, List<MultipartContent.Part> parts) {
        this.delegate = BodyPublishers.ofInputStream(() -> Channels.newInputStream(
                new MultipartFormDataChannel(boundary, parts)));
    }

    @Override
    public void subscribe(Subscriber<? super ByteBuffer> s) {
        delegate.subscribe(s);
    }

    @Override
    public long contentLength() {
        return delegate.contentLength();
    }

}


class MultipartFormDataChannel implements ReadableByteChannel {
    private static final int BOUNDARY = 0;
    private static final int HEADERS = 1;
    private static final int BODY = 2;
    private static final int DONE = 3;
    private static final ByteBuffer CRLF = ByteBuffer.wrap("\r\n".getBytes());

    private final Iterator<MultipartContent.Part> parts;

    private int state;
    private ByteBuffer buf;
    private MultipartContent.Part current;
    private ReadableByteChannel channel;

    private final ByteBuffer boundaryNext, boundaryLast;

    MultipartFormDataChannel(String boundary, Iterable<MultipartContent.Part> parts) {
        this.parts = parts.iterator();
        this.boundaryNext = ByteBuffer.wrap(("--" + boundary + "\r\n").getBytes(StandardCharsets.ISO_8859_1));
        this.boundaryLast = ByteBuffer.wrap(("--" + boundary + "--\r\n").getBytes(StandardCharsets.ISO_8859_1));
    }

    @Override
    public void close() throws IOException {
        ReadableByteChannel channel;
        if ((channel = this.channel) != null) {
            channel.close();
            this.channel = null;
        }
        state = DONE;
    }

    @Override
    public boolean isOpen() {
        return state != DONE;
    }

    @Override
    public int read(ByteBuffer output) throws IOException {
        while (true) {
            ByteBuffer buf;
            if ((buf = this.buf) != null && buf.hasRemaining()) {
                val n = Math.min(buf.remaining(), output.remaining());
                val slice = buf.slice();
                slice.limit(n);
                output.put(slice);
                buf.position(buf.position() + n);
                return n;
            }

            switch (this.state) {
                case BOUNDARY:
                    if (this.parts.hasNext()) {
                        this.current = this.parts.next();
                        this.buf = boundaryNext.rewind();
                        this.state = HEADERS;
                    } else {
                        this.buf = boundaryLast.rewind();
                        this.state = DONE;
                    }
                    break;
                case HEADERS:
                    this.buf = ByteBuffer.wrap(this.currentHeaders().getBytes(StandardCharsets.UTF_8));
                    this.state = BODY;
                    break;
                case BODY:
                    if (this.channel == null) {
                        this.channel = this.current.content().open();
                    }

                    val n = this.channel.read(output);
                    if (n == -1) {
                        this.channel.close();
                        this.channel = null;
                        this.buf = CRLF.rewind();
                        this.state = BOUNDARY;
                    } else {
                        return n;
                    }
                    break;
                case DONE:
                    return -1;
            }
        }
    }

    String currentHeaders() {
        val current = this.current;
        if (current == null) {
            throw new IllegalStateException();
        }

        val contentType = current.content();
        val fileName = current.fileName();

        val sb = new StringBuilder();
        sb.append("Content-Disposition: form-data; name=\"").append(current.name()).append("\"");
        if (fileName != null) {
            sb.append("; filename=\"").append(fileName).append("\"");
        }
        sb.append("\r\n");
        if (contentType != null) {
            sb.append("Content-Type: ").append(contentType).append("\r\n");
        }
        sb.append("\r\n");

        return sb.toString();
    }
}
