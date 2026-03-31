package io.telegramkt.model.poll.input

import io.telegramkt.model.ParseMode
import io.telegramkt.model.message.entity.MessageEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InputPollOption(
    @SerialName("text") val text: String,
    @SerialName("text_parse_model") val textParseMode: ParseMode? = null,
    @SerialName("text_entities") val textEntities: List<MessageEntity>? = null,
)
