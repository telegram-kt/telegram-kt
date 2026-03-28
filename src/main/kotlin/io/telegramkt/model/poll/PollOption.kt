package io.telegramkt.model.poll

import io.telegramkt.model.message.entity.MessageEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PollOption(
    @SerialName("text") val text: String,
    @SerialName("text_entities") val textEntities: List<MessageEntity>? = null,
    @SerialName("voter_count") val voterCount: Int
)
