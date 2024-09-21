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

import lombok.SneakyThrows;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

/**
 * @author whilein
 */
public abstract class ExceptionSneakyPropagatingContentVisitor<R> implements ContentVisitor<R> {

    protected abstract R visitInputStream0(String contentType, long size, InputStream is) throws Exception;

    protected abstract R visitMultipart0(String contentType, String boundary, List<MultipartContent.Part> parts) throws Exception;

    protected abstract R visitFile0(String contentType, long size, Path path) throws Exception;

    protected abstract R visitBytes0(String contentType, byte[] bytes, int off, int len) throws Exception;

    @SneakyThrows
    private static <R> R propagateSneaky(Throwable e) {
        throw e;
    }

    @Override
    public final R visitInputStream(String contentType, long size, InputStream is) {
        try {
            return visitInputStream0(contentType, size, is);
        } catch (Exception e) {
            return propagateSneaky(e);
        }
    }

    @Override
    public final R visitMultipart(String contentType, String boundary, List<MultipartContent.Part> parts) {
        try {
            return visitMultipart0(contentType, boundary, parts);
        } catch (Exception e) {
            return propagateSneaky(e);
        }
    }

    @Override
    public final R visitFile(String contentType, long size, Path path) {
        try {
            return visitFile0(contentType, size, path);
        } catch (Exception e) {
            return propagateSneaky(e);
        }
    }

    @Override
    public final R visitBytes(String contentType, byte[] bytes, int off, int len) {
        try {
            return visitBytes0(contentType, bytes, off, len);
        } catch (Exception e) {
            return propagateSneaky(e);
        }
    }

}
