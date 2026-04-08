package io.telegramkt.model.bot.description

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BotShortDescription(
    @SerialName("short_description") val value: String
) {
    override fun toString(): String = value
}