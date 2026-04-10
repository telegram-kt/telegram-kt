package io.telegramkt.model.story

import io.telegramkt.model.chat.Chat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Story(
    @SerialName("chat") val chat: Chat,
    @SerialName("id") val id: Int,
)