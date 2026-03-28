package io.telegramkt.model.chat.member

import io.telegramkt.model.chat.Chat
import io.telegramkt.model.chat.invite.ChatInviteLink
import io.telegramkt.model.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMemberUpdated(
    @SerialName("chat") val chat: Chat,
    @SerialName("from") val from: User,
    @SerialName("date") val date: Int,
    @SerialName("old_chat_member") val oldChatMember: ChatMember,
    @SerialName("new_chat_member") val newChatMember: ChatMember,
    @SerialName("invite_link") val inviteLink: ChatInviteLink,
)
