/*
 *    Copyright 2025 Whilein
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

import java.util.Arrays;
import java.util.List;

/**
 * @author _Novit_ (novitpw)
 */
public enum PhotoSize {

    MINIMAL,
    NORMAL,
    MAXIMUM;

    public static final List<PhotoSize> VALUES = Arrays.asList(PhotoSize.values());

}
