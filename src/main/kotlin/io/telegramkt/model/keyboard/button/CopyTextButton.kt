package io.telegramkt.model.keyboard.button

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CopyTextButton(
    @SerialName("text") val text: String
)
