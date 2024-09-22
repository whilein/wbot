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

package wbot.util;

import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author whilein
 */
final class MimeUtilsTests {

    @Test
    @SneakyThrows
    void determineContentType() {
        assertEquals("image/png",
                MimeUtils.determineContentType("image.png", InputStream.nullInputStream()));

        val pngBytes = new ByteArrayOutputStreamEx();
        ImageIO.write(
                new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR),
                "png",
                pngBytes
        );
        assertEquals("image/png",
                MimeUtils.determineContentType("image", pngBytes.toInputStream()));

        assertEquals("application/octet-stream",
                MimeUtils.determineContentType("image", InputStream.nullInputStream()));
    }

    @Test
    void getContentTypeByExtension() {
        assertEquals("image/png", MimeUtils.getContentTypeByExtension("png"));
        assertEquals("text/html", MimeUtils.getContentTypeByExtension("html"));
        assertEquals("application/x-java-archive", MimeUtils.getContentTypeByExtension("jar"));
    }

}
