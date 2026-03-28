package io.telegramkt.model.chat.member

import io.telegramkt.model.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
@JsonClassDiscriminator("status")
sealed class ChatMember {
    abstract val user: User

    @Serializable
    @SerialName("creator")
    data class Owner(
        override val user: User,
        @SerialName("is_anonymous") val isAnonymous: Boolean,
        @SerialName("custom_title") val customTitle: String? = null
    ) : ChatMember()

    @Serializable
    @SerialName("administrator")
    data class Administrator(
        override val user: User,
        @SerialName("can_be_edited") val canBeEdited: Boolean,
        @SerialName("is_anonymous") val isAnonymous: Boolean,
        @SerialName("can_manage_chat") val canManageChat: Boolean,
        @SerialName("can_delete_messages") val canDeleteMessages: Boolean,
        @SerialName("can_manage_video_chats") val canManageVideoChats: Boolean,
        @SerialName("can_restrict_members") val canRestrictMembers: Boolean,
        @SerialName("can_promote_members") val canPromoteMembers: Boolean,
        @SerialName("can_change_info") val canChangeInfo: Boolean,
        @SerialName("can_invite_users") val canInviteUsers: Boolean,
        @SerialName("can_post_messages") val canPostMessages: Boolean? = null,
        @SerialName("can_edit_messages") val canEditMessages: Boolean? = null,
        @SerialName("can_pin_messages") val canPinMessages: Boolean? = null,
        @SerialName("can_post_stories") val canPostStories: Boolean? = null,
        @SerialName("can_edit_stories") val canEditStories: Boolean? = null,
        @SerialName("can_delete_stories") val canDeleteStories: Boolean? = null,
        @SerialName("can_manage_topics") val canManageTopics: Boolean? = null,
        @SerialName("custom_title") val customTitle: String? = null
    ) : ChatMember()

    @Serializable
    @SerialName("member")
    data class Member(
        override val user: User,
        @SerialName("until_date") val untilDate: Int? = null
    ) : ChatMember()

    @Serializable
    @SerialName("restricted")
    data class Restricted(
        override val user: User,
        @SerialName("is_member") val isMember: Boolean,
        @SerialName("can_send_messages") val canSendMessages: Boolean,
        @SerialName("can_send_audios") val canSendAudios: Boolean,
        @SerialName("can_send_documents") val canSendDocuments: Boolean,
        @SerialName("can_send_photos") val canSendPhotos: Boolean,
        @SerialName("can_send_videos") val canSendVideos: Boolean,
        @SerialName("can_send_video_notes") val canSendVideoNotes: Boolean,
        @SerialName("can_send_voice_notes") val canSendVoiceNotes: Boolean,
        @SerialName("can_send_polls") val canSendPolls: Boolean,
        @SerialName("can_send_other_messages") val canSendOtherMessages: Boolean,
        @SerialName("can_add_web_page_previews") val canAddWebPagePreviews: Boolean,
        @SerialName("can_change_info") val canChangeInfo: Boolean,
        @SerialName("can_invite_users") val canInviteUsers: Boolean,
        @SerialName("can_pin_messages") val canPinMessages: Boolean,
        @SerialName("can_manage_topics") val canManageTopics: Boolean,
        @SerialName("until_date") val untilDate: Int
    ) : ChatMember()

    @Serializable
    @SerialName("left")
    data class Left(
        override val user: User
    ) : ChatMember()

    @Serializable
    @SerialName("kicked")
    data class Banned(
        override val user: User,
        @SerialName("until_date") val untilDate: Int
    ) : ChatMember()
}
