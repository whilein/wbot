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

import wbot.platform.PlatformType;

/**
 * @author whilein
 */
public interface IdentityHolder {

    default Identity getIdentity() {
        return new Identity(getValue(), getPlatform());
    }

    long getValue();

    PlatformType getPlatform();

    boolean isChat();

    boolean isBot();

    boolean isUser();

}
