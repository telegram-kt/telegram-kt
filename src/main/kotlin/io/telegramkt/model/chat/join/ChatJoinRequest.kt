package io.telegramkt.model.chat.join

import io.telegramkt.model.chat.Chat
import io.telegramkt.model.chat.invite.ChatInviteLink
import io.telegramkt.model.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatJoinRequest(
    @SerialName("chat") val chat: Chat,
    @SerialName("from") val from: User,
    @SerialName("user_chat_id") val userChatId: Int,
    @SerialName("date") val date: Int,
    @SerialName("bio") val bio: String? = null,
    @SerialName("invite_link") val inviteLink: ChatInviteLink? = null,
)
