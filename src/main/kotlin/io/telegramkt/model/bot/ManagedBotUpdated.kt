package io.telegramkt.model.bot

import io.telegramkt.model.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManagedBotUpdated(
    @SerialName("user") val user: User,
    @SerialName("bot") val bot: User,
)
