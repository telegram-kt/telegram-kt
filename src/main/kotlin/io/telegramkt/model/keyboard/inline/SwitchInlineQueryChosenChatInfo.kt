package io.telegramkt.model.keyboard.inline

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SwitchInlineQueryChosenChatInfo(
    @SerialName("query") val query: String? = null,
    @SerialName("allow_user_chats") val allowUserChats: Boolean? = null,
    @SerialName("allow_bot_chats") val allowBotChats: Boolean? = null,
    @SerialName("allow_group_chats") val allowGroupChats: Boolean? = null,
    @SerialName("allow_channel_chats") val allowChannelChats: Boolean? = null,
)
