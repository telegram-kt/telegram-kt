package io.telegramkt.model.chat.administrator

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatAdministratorRights(
    @SerialName("is_anonymous") val isAnonymous: Boolean,
    @SerialName("can_manage_chat") val canManageChat: Boolean,
    @SerialName("can_delete_messages") val canDeleteMessages: Boolean,
    @SerialName("can_manage_video_chats") val canManageVideoChats: Boolean,
    @SerialName("can_restrict_members") val canRestrictMembers: Boolean,
    @SerialName("can_promote_members") val canPromoteMembers: Boolean,
    @SerialName("can_change_info") val canChangeInfo: Boolean,
    @SerialName("can_invite_users") val canInviteUsers: Boolean,
    @SerialName("can_post_stories") val canPostStories: Boolean? = null,
    @SerialName("can_edit_stories") val canEditStories: Boolean? = null,
    @SerialName("can_delete_stories") val canDeleteStories: Boolean? = null,
    @SerialName("can_post_messages") val canPostMessages: Boolean? = null,
    @SerialName("can_edit_messages") val canEditMessages: Boolean? = null,
    @SerialName("can_pin_messages") val canPinMessages: Boolean? = null,
    @SerialName("can_manage_topics") val canManageTopics: Boolean? = null
)
