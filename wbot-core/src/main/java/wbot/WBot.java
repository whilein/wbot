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

package wbot;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wbot.command.CommandEventHandler;
import wbot.command.CommandKeyboardButtonPayloadCodec;
import wbot.command.CommandManager;
import wbot.command.SimpleCommandKeyboardButtonPayloadCodec;
import wbot.command.SimpleCommandManager;
import wbot.event.EventHandler;
import wbot.event.SimpleEventDispatcher;
import wbot.http.DefaultHttpClient;
import wbot.http.HttpClient;
import wbot.platform.Platform;
import wbot.platform.PlatformType;
import wbot.platform.telegram.TelegramClient;
import wbot.platform.telegram.TelegramPlatform;
import wbot.platform.vk.VkClient;
import wbot.platform.vk.VkPlatform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class WBot {

    Lock lifecycleLock = new ReentrantLock();

    HttpClient httpClient;

    List<Platform> platforms;
    Map<PlatformType, Platform> type2PlatformMap;

    @NonFinal
    List<Thread> threads;

    @Getter
    CommandManager commandManager;

    private void start0() {
        if (threads != null) return;

        httpClient.start();

        val threads = new ArrayList<Thread>(platforms.size());
        for (val platform : platforms) {
            val thread = new Thread(platform, platform.getType().getDisplayName() + " Platform");
            thread.start();

            threads.add(thread);
        }
        this.threads = threads;
    }

    public void start() {
        Lock lock;
        (lock = lifecycleLock).lock();

        try {
            start0();
        } finally {
            lock.unlock();
        }
    }

    private void stop0() {
        if (threads == null) return;

        httpClient.stop();

        for (val thread : threads) {
            thread.interrupt();
        }
        threads = null;
    }

    public void stop() {
        Lock lock;
        (lock = lifecycleLock).lock();

        try {
            stop0();
        } finally {
            lock.unlock();
        }
    }

    public @NotNull Platform getPlatform(@NotNull PlatformType type) {
        val platform = type2PlatformMap.get(type);
        if (platform == null) {
            throw new IllegalArgumentException("Platform " + type.getDisplayName() + " not enabled.");
        }

        return platform;
    }

    public @Unmodifiable @NotNull List<@NotNull Platform> getPlatforms() {
        return Collections.unmodifiableList(platforms);
    }

    @Accessors(fluent = true)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static final class Builder {

        @Setter
        @NonFinal
        Logger logger;

        @Setter
        @NonFinal
        HttpClient httpClient;

        @Setter
        @NonFinal
        JsonMapper jsonMapper;

        @Setter
        @NonFinal
        Logger telegramLogger;
        @NonFinal
        String telegramToken;

        @Setter
        @NonFinal
        Logger vkontakteLogger;
        @NonFinal
        long vkontakteDocumentOwnerId;
        @NonFinal
        String vkontakteToken;

        @NonFinal
        CommandManager commandManager;

        @NonFinal
        CommandKeyboardButtonPayloadCodec keyboardButtonPayloadCodec;

        @Setter
        @NonFinal
        boolean registerCommandEventHandler = true;

        Set<EventHandler> customEventHandlers = new HashSet<>();

        public Builder customEventHandler(EventHandler eventHandler) {
            this.customEventHandlers.add(eventHandler);
            return this;
        }

        public Builder customCommandManager(CommandManager commandManager) {
            this.commandManager = commandManager;
            return this;
        }

        public Builder customKeyboardButtonPayloadCodec(CommandKeyboardButtonPayloadCodec keyboardButtonPayloadCodec) {
            this.keyboardButtonPayloadCodec = keyboardButtonPayloadCodec;
            return this;
        }

        public Builder enableTelegram(String token) {
            this.telegramToken = token;
            return this;
        }

        public Builder enableVkontakte(long documentOwnerId, String token) {
            this.vkontakteDocumentOwnerId = documentOwnerId;
            this.vkontakteToken = token;
            return this;
        }

        public WBot build() {
            Logger logger;
            if ((logger = this.logger) == null) {
                logger = LoggerFactory.getLogger("wbot");
            }
            HttpClient httpClient;
            if ((httpClient = this.httpClient) == null) {
                httpClient = new DefaultHttpClient(-1);
            }
            JsonMapper jsonMapper;
            if ((jsonMapper = this.jsonMapper) == null) {
                jsonMapper = new JsonMapper();
            }

            CommandKeyboardButtonPayloadCodec keyboardButtonPayloadCodec;
            if ((keyboardButtonPayloadCodec = this.keyboardButtonPayloadCodec) == null) {
                keyboardButtonPayloadCodec = new SimpleCommandKeyboardButtonPayloadCodec(jsonMapper);
            }

            CommandManager commandManager;
            if ((commandManager = this.commandManager) == null) {
                commandManager = new SimpleCommandManager(keyboardButtonPayloadCodec);
            }

            val eventHandlers = new HashSet<>(this.customEventHandlers);

            if (registerCommandEventHandler) {
                eventHandlers.add(new CommandEventHandler(commandManager));
            }

            val eventDispatcher = new SimpleEventDispatcher(logger, eventHandlers);

            val platforms = new ArrayList<Platform>();

            if (telegramToken != null) {
                Logger telegramLogger;
                if ((telegramLogger = this.telegramLogger) == null) {
                    telegramLogger = LoggerFactory.getLogger("wbot.telegram");
                }

                platforms.add(new TelegramPlatform(
                        telegramLogger,
                        new TelegramClient(telegramToken, httpClient, jsonMapper),
                        eventDispatcher
                ));
            }

            if (vkontakteToken != null) {
                Logger vkontakteLogger;
                if ((vkontakteLogger = this.vkontakteLogger) == null) {
                    vkontakteLogger = LoggerFactory.getLogger("wbot.vkontakte");
                }

                platforms.add(new VkPlatform(
                        vkontakteDocumentOwnerId,
                        vkontakteLogger,
                        new VkClient(vkontakteToken, httpClient, jsonMapper),
                        eventDispatcher
                ));
            }

            val type2PlatformMap = platforms.stream()
                    .collect(Collectors.toMap(
                            Platform::getType,
                            Function.identity()
                    ));

            return new WBot(httpClient, platforms, type2PlatformMap, commandManager);
        }

    }

}
