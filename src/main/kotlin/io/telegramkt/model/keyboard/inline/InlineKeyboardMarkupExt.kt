package io.telegramkt.model.keyboard.inline

import io.telegramkt.model.keyboard.reply.InlineKeyboardMarkup

fun InlineKeyboardMarkup.Companion.create(
    vararg row: List<InlineKeyboardButton>
): InlineKeyboardMarkup = InlineKeyboardMarkup(row.toList())

fun inlineKeyboard(block: InlineKeyboardBuilder.() -> Unit): InlineKeyboardMarkup {
    val builder = InlineKeyboardBuilder()
    builder.block()
    return InlineKeyboardMarkup(builder.rows)
}