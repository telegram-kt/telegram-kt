package io.telegramkt.model.keyboard.reply

import io.telegramkt.model.keyboard.reply.ReplyMarkup
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReplyKeyboardRemove(
    @SerialName("remove_keyboard") val removeKeyboard: Boolean = true,
    @SerialName("selective") val selective: Boolean? = null
) : ReplyMarkup
