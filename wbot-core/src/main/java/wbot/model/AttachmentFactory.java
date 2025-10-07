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

package wbot.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbot.http.BytesContent;
import wbot.http.EmbeddableContent;
import wbot.http.FileContent;
import wbot.http.InputStreamContent;
import wbot.util.ByteArrayOutputStreamEx;
import wbot.util.MimeUtils;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class AttachmentFactory {

    Executor executor;

    public Attachment fromByteArray(AttachmentType type, String fileName, String contentType, byte[] bytes) {
        return new BytesAttachment(type, fileName, contentType, bytes);
    }

    public Attachment fromByteArray(AttachmentType type, String fileName, byte[] bytes) {
        val contentType = MimeUtils.determineContentType(fileName, new ByteArrayInputStream(bytes));

        return fromByteArray(type, fileName, contentType, bytes);
    }

    public Attachment fromInputStream(
            @NotNull AttachmentType type,
            @NotNull String fileName,
            @Nullable String contentType,
            long size,
            @NotNull Supplier<InputStream> is
    ) {
        return new InputStreamAttachment(executor, type, fileName, contentType, size, is);
    }

    public Attachment fromFile(@NotNull AttachmentType type, @NotNull Path path, @Nullable String contentType) {
        return new FileAttachment(executor, type, path, contentType);
    }

    public Attachment fromImage(
            @NotNull AttachmentType type,
            @NotNull String fileName,
            @NotNull String contentType,
            @NotNull BufferedImage image
    ) {
        return new BufferedImageAttachment(executor, type, fileName, contentType, image);
    }

    public Attachment fromImage(AttachmentType type, String format, BufferedImage image) {
        return fromImage(type, "image." + format, MimeUtils.getContentTypeByExtension(format), image);
    }

    @Accessors(fluent = true)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor
    private static final class BufferedImageAttachment implements Attachment {

        Executor executor;

        @Getter
        AttachmentType type;

        @Getter
        String fileName;

        String contentType;

        BufferedImage image;

        @Override
        public @NotNull CompletableFuture<EmbeddableContent> createContent() {
            return CompletableFuture.supplyAsync(() -> {
                val writers = ImageIO.getImageWritersByMIMEType(contentType);
                if (!writers.hasNext()) {
                    throw new IllegalArgumentException("No image writer found for " + contentType);
                }

                try (val content = new ByteArrayOutputStreamEx();
                     val output = new MemoryCacheImageOutputStream(content)) {
                    val writer = writers.next();
                    writer.setOutput(output);

                    try {
                        writer.write(image);
                    } finally {
                        writer.dispose();
                    }

                    return new InputStreamContent(contentType, content.size(), content.toInputStream());
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            }, executor);
        }
    }

    @Accessors(fluent = true)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor
    private static final class InputStreamAttachment implements Attachment {

        Executor executor;

        @Getter
        AttachmentType type;

        @Getter
        String fileName;

        String contentType;

        long size;

        Supplier<InputStream> is;

        @Override
        public @NotNull CompletableFuture<EmbeddableContent> createContent() {
            return CompletableFuture.supplyAsync(() -> {
                val is = this.is.get();

                String contentType;
                if ((contentType = this.contentType) == null) {
                    contentType = MimeUtils.determineContentType(fileName, is);
                }

                return new InputStreamContent(contentType, size, is);
            }, executor);
        }
    }

    @Accessors(fluent = true)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor
    private static final class FileAttachment implements Attachment {

        Executor executor;

        @Getter
        AttachmentType type;

        Path path;

        String contentType;

        @Override
        public @NotNull String fileName() {
            return path.getFileName().toString();
        }

        @Override
        public @NotNull CompletableFuture<EmbeddableContent> createContent() {
            return CompletableFuture.supplyAsync(() -> {
                val path = this.path;
                try {
                    String contentType;
                    if ((contentType = this.contentType) == null) {
                        contentType = Files.probeContentType(path);
                    }
                    return new FileContent(contentType, Files.size(path), path);
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            }, executor);
        }

    }

    @Accessors(fluent = true)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor
    private static final class BytesAttachment implements Attachment {

        @Getter
        AttachmentType type;

        @Getter
        String fileName;

        String contentType;

        byte[] bytes;

        @Override
        public @NotNull CompletableFuture<EmbeddableContent> createContent() {
            return CompletableFuture.completedFuture(new BytesContent(contentType, bytes));
        }
    }


}
