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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author whilein
 */
@UtilityClass
public class MimeUtils {

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private static final Map<String, String> EXTENSION_TO_MIME_TYPE;

    static {
        EXTENSION_TO_MIME_TYPE = loadMimeTypes();
    }

    private static Map<String, String> loadMimeTypes() {
        InputStream is = MimeUtils.class.getClassLoader().getResourceAsStream("/mime_types.json");
        if (is == null) {
            return null;
        }

        Map<String, String[]> mimeTypes;

        try {
            mimeTypes = new JsonMapper().readValue(is, new TypeReference<HashMap<String, String[]>>() {});
        } catch (IOException e) {
            return null;
        }

        val extensionToMimeTypes = new HashMap<String, String>();

        for (val entry : mimeTypes.entrySet()) {
            for (val extension : entry.getValue()) {
                extensionToMimeTypes.put(extension, entry.getKey());
            }
        }

        return extensionToMimeTypes;
    }

    public String getContentTypeByExtension(String extension) {
        val extensionToMimeType = EXTENSION_TO_MIME_TYPE;
        return extensionToMimeType != null
                ? extensionToMimeType.getOrDefault(extension, DEFAULT_CONTENT_TYPE)
                : DEFAULT_CONTENT_TYPE;
    }

    private String getExtension(String filename) {
        val extension = filename.lastIndexOf('.');
        if (extension == -1) {
            return "";
        }

        return filename.substring(extension + 1).toLowerCase();
    }

    public String determineContentType(String filename, InputStream content) {
        String contentType = null;

        try {
            contentType = URLConnection.guessContentTypeFromStream(content);
        } catch (IOException ignored) {
        }

        if (contentType == null) {
            contentType = getContentTypeByExtension(getExtension(filename));
        }

        return contentType;
    }

}
