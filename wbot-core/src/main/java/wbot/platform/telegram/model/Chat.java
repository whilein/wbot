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

package wbot.platform.telegram.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import wbot.model.IdentityHolder;
import wbot.platform.PlatformType;

import java.util.List;

/**
 * @author whilein
 */
@Value
@Builder
@Jacksonized
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Chat implements IdentityHolder {

    long id;
    String type;
    String title;
    String firstName;
    String lastName;
    String username;
    boolean isForum;
    List<String> activeUsernames;
    String bio;
    String description;
    String inviteLink;
    int slowDelayMode;
    Message pinnedMessage;
    boolean hasHiddenMembers;
    boolean hasProtectedContent;
    boolean hasRestrictedVoiceAndVideoMessages;
    boolean hasPrivateForwards;
    boolean joinByRequest;
    boolean joinToSendMessages;
    int linkedChatId;
    boolean canSetStickerSet;
    String stickerSetName;
    boolean hasAggressiveAntiSpamEnabled;
    int messageAutoDeleteTime;
    String emojiStatusCustomEmojiId;
    int emojiStatusExpirationDate;

    @Override
    public long getValue() {
        return id;
    }

    @Override
    public boolean isChat() {
        return type.equals("group") || type.equals("supergroup");
    }

    @Override
    public boolean isBot() {
        return false;
    }

    @Override
    public boolean isUser() {
        return type.equals("private");
    }

    @Override
    public PlatformType getPlatform() {
        return PlatformType.TELEGRAM;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
