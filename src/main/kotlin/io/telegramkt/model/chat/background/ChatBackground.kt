package io.telegramkt.model.chat.background

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatBackground(
    @SerialName("type") val type: BackgroundType
)
