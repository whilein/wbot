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

package wbot.platform.vk.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * @author _Novit_ (novitpw)
 */
@Value
@Builder
@Jacksonized
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupLongPollSettings {
    boolean isEnabled;
    Events events;

    @Value
    @Builder
    @Jacksonized
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Events {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean messageNew;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean messageReply;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean messageAllow;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean messageDeny;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean photoNew;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean audioNew;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean videoNew;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean wallReplyNew;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean wallReplyEdit;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean wallReplyDelete;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean wallPostNew;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean wallRepost;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean boardPostNew;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean boardPostEdit;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean boardPostDelete;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean boardPostRestore;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean photoCommentNew;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean photoCommentEdit;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean photoCommentDelete;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean photoCommentRestore;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean videoCommentNew;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean videoCommentEdit;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean videoCommentDelete;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean videoCommentRestore;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean marketCommentNew;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean marketCommentEdit;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean marketCommentDelete;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean marketCommentRestore;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean pollVoteNew;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean groupJoin;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean groupLeave;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean userBlock;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean userUnblock;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean groupChangeSettings;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean groupChangePhoto;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean groupOfficersEdit;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean donutSubscriptionCreate;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean donutSubscriptionProlonged;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean donutSubscriptionExpired;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean donutSubscriptionCancelled;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean subscriptionPriceChanged;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean donutMoneyWithdraw;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        boolean donutMoneyWithdrawError;

        public boolean hasAny() {
            return messageNew
                   || messageReply
                   || messageAllow
                   || messageDeny
                   || photoNew
                   || audioNew
                   || videoNew
                   || wallReplyNew
                   || wallReplyEdit
                   || wallReplyDelete
                   || wallPostNew
                   || wallRepost
                   || boardPostNew
                   || boardPostEdit
                   || boardPostDelete
                   || boardPostRestore
                   || photoCommentNew
                   || photoCommentEdit
                   || photoCommentDelete
                   || photoCommentRestore
                   || videoCommentNew
                   || videoCommentEdit
                   || videoCommentDelete
                   || videoCommentRestore
                   || marketCommentNew
                   || marketCommentEdit
                   || marketCommentDelete
                   || marketCommentRestore
                   || pollVoteNew
                   || groupJoin
                   || groupLeave
                   || userBlock
                   || userUnblock
                   || groupChangeSettings
                   || groupChangePhoto
                   || groupOfficersEdit
                   || donutSubscriptionCreate
                   || donutSubscriptionProlonged
                   || donutSubscriptionExpired
                   || donutSubscriptionCancelled
                   || subscriptionPriceChanged
                   || donutMoneyWithdraw
                   || donutMoneyWithdrawError;
        }

    }
}
