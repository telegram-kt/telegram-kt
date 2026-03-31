package io.telegramkt.model.checklist

import io.telegramkt.model.chat.Chat
import io.telegramkt.model.message.entity.MessageEntity
import io.telegramkt.model.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChecklistTask(
    @SerialName("id") val id: Int,
    @SerialName("text") val text: String,
    @SerialName("text_entities") val textEntities: List<MessageEntity>? = null,
    @SerialName("completed_by_user") val completedByUser: User? = null,
    @SerialName("completed_by_chat") val completedByChat: Chat? = null,
    @SerialName("completion_date") val completionData: Int? = null,
)
