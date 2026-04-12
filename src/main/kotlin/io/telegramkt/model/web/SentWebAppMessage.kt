package io.telegramkt.model.web

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SentWebAppMessage(
    @SerialName("inline_message_id") val inlineMessageId: String? = null,
)
