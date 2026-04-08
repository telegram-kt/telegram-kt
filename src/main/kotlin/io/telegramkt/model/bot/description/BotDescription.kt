package io.telegramkt.model.bot.description

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BotDescription(
    @SerialName("description") val value: String
) {
    override fun toString(): String = value
}