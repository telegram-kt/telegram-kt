package io.telegramkt.model.keyboard.button.prepared

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PreparedKeyboardButton(
    @SerialName("id") val id: String,
)