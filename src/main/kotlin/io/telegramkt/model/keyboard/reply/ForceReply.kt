package io.telegramkt.model.keyboard.reply

import io.telegramkt.model.keyboard.reply.ReplyMarkup
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForceReply(
    @SerialName("force_reply") val forceReply: Boolean = true,
    @SerialName("input_field_placeholder") val inputFieldPlaceholder: String? = null,
    @SerialName("selective") val selective: Boolean? = null
) : ReplyMarkup