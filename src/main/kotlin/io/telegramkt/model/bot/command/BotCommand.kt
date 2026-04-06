package io.telegramkt.model.bot.command

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BotCommand(
    @SerialName("command") val command: String,
    @SerialName("description") val description: String,
)
