package io.telegramkt.model.message.inline.prepared

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PreparedInlineMessage(
    @SerialName("id") val id: String,
)