package io.telegramkt.model.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageOrigin(
    @SerialName("type") val type: String
)
