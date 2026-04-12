package io.telegramkt.model.keyboard.button.prepared

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PreparedKeyboardButtonType {
    @SerialName("user") USER,
    @SerialName("bot") BOT,
    @SerialName("group") GROUP,
    @SerialName("channel") CHANNEL
}