package io.telegramkt.model.chat.boost

import io.telegramkt.model.chat.Chat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatBoostUpdated(
    @SerialName("chat") val chat: Chat,
    @SerialName("boost") val boost: ChatBoost
)
