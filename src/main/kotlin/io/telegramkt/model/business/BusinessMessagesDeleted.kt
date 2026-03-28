package io.telegramkt.model.business

import io.telegramkt.model.chat.Chat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BusinessMessagesDeleted(
    @SerialName("business_connection_id") val businessConnectionId: String,
    @SerialName("chat") val chat: Chat,
    @SerialName("message_ids") val messageIds: List<Int>
)
