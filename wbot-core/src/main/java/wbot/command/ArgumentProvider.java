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

package wbot.command;

import lombok.val;

import java.util.StringJoiner;

/**
 * @author whilein
 */
public interface ArgumentProvider {

    String argument(int i);

    int argumentCount();

    default String joinArguments(int start) {
        val joiner = new StringJoiner(" ");

        for (int i = start, j = argumentCount(); i < j; i++) {
            joiner.add(argument(i));
        }

        return joiner.toString();
    }
}
