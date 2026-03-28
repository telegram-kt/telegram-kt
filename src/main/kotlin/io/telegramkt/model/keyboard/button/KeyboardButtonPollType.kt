package io.telegramkt.model.keyboard.button

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KeyboardButtonPollType(
    @SerialName("type") val type: String? = null
)
