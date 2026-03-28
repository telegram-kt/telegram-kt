package io.telegramkt.model.business

import io.telegramkt.model.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BusinessConnection(
    @SerialName("id") val id: String,
    @SerialName("user") val user: User,
    @SerialName("user_chat_id") val userChatId: Int,
    @SerialName("date") val date: Int,
    @SerialName("rights") val rights: BusinessBotRights? = null,
    @SerialName("is_enabled") val isEnabled: Boolean,
)
