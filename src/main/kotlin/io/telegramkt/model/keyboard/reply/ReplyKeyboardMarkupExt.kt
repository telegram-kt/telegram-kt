package io.telegramkt.model.keyboard.reply

fun replyKeyboard(block: ReplyKeyboardBuilder.() -> Unit): ReplyKeyboardMarkup {
    return ReplyKeyboardBuilder().apply(block).build()
}