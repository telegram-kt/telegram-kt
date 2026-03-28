package io.telegramkt.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseParameters(
    @SerialName("migrate_to_chat_id") val migrateToChatId: Long? = null,
    @SerialName("retry_after") val retryAfter: Int? = null,
)
