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

/**
 * @author _Novit_ (novitpw)
 */
public interface ImageDimensions {

    int getWidth();

    int getHeight();

    default long calculateArea() {
        return (long) getWidth() * getHeight();
    }

    default int maxSide() {
        return Math.max(getWidth(), getHeight());
    }

    default int minSide() {
        return Math.min(getWidth(), getHeight());
    }

}
