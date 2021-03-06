/*
 *    Copyright 2022 Whilein
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

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.io.IoBuilder;
import w.bot.SimpleVkBot;
import w.bot.VkBotConfig;
import w.config.SimpleFileConfig;

/**
 * @author whilein
 */
@Log4j2
@UtilityClass
public class Main {

    @SneakyThrows
    public static void main(final String[] args) {
        System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

        System.setErr(IoBuilder.forLogger(log).setLevel(Level.ERROR).buildPrintStream());
        System.setOut(IoBuilder.forLogger(log).setLevel(Level.INFO).buildPrintStream());

        val config = SimpleFileConfig.create("config.yml");
        config.saveDefaults("/config.yml");

        val bot = SimpleVkBot.create(config.asType(VkBotConfig.class));
        bot.getLongPoll().start();
    }

}
