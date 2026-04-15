package io.telegramkt.model.message

import io.telegramkt.model.message.entity.MessageEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Contains information about the quoted part of a message that is being replied to.
 *
 * See https://core.telegram.org/bots/api#textquote
 */
@Serializable
data class TextQuote(
    @SerialName("text") val text: String,
    @SerialName("entities") val entities: List<MessageEntity>? = null,
    @SerialName("position") val position: Int,
    @SerialName("is_manual") val isManual: Boolean? = null,
)
