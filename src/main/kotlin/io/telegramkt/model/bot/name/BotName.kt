package io.telegramkt.model.bot.name

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BotName(
    @SerialName("name") val value: String
) {
    override fun toString(): String = value
}
