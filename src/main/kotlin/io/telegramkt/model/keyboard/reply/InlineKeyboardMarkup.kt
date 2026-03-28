package io.telegramkt.model.keyboard.reply

import io.telegramkt.model.keyboard.inline.InlineKeyboardButton
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InlineKeyboardMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboardButton>>
) : ReplyMarkup