package io.telegramkt.model.checklist.input

import io.telegramkt.model.ParseMode
import io.telegramkt.model.message.entity.MessageEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InputChecklistTask(
    @SerialName("id") val id: Int,
    @SerialName("text") val text: String,
    @SerialName("parse_mode") val parseMode: ParseMode? = null,
    @SerialName("text_entities") val textEntities: List<MessageEntity>? = null,
)
