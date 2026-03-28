package io.telegramkt.model.keyboard.reply

import io.telegramkt.model.keyboard.button.KeyboardButton
import io.telegramkt.model.keyboard.button.KeyboardButtonPollType

class ReplyKeyboardBuilder {
    private val _rows = mutableListOf<MutableList<KeyboardButton>>()
    private var currentRow: MutableList<KeyboardButton>? = null

    var isPersistent: Boolean? = null
    var resizeKeyboard: Boolean? = null
    var oneTimeKeyboard: Boolean? = null
    var inputFieldPlaceholder: String? = null
    var selective: Boolean? = null

    fun row(vararg buttons: KeyboardButton) {
        if (currentRow != null && currentRow!!.isNotEmpty()) {
            _rows.add(currentRow!!)
        }
        currentRow = buttons.toMutableList()
    }

    private fun addToCurrentRow(button: KeyboardButton) {
        if (currentRow == null) currentRow = mutableListOf()
        currentRow!!.add(button)
    }

    fun simpleButton(text: String): KeyboardButton.Simple =
        KeyboardButton.Simple(text).also { addToCurrentRow(it) }

    fun contactButton(text: String): KeyboardButton.RequestContact =
        KeyboardButton.RequestContact(text).also { addToCurrentRow(it) }

    fun locationButton(text: String): KeyboardButton.RequestLocation =
        KeyboardButton.RequestLocation(text).also { addToCurrentRow(it) }

    fun pollButton(text: String, type: String? = null): KeyboardButton.RequestPoll =
        KeyboardButton.RequestPoll(text, KeyboardButtonPollType(type)).also { addToCurrentRow(it) }

    fun build(): ReplyKeyboardMarkup {
        currentRow?.let { if (it.isNotEmpty()) _rows.add(it) }
        return ReplyKeyboardMarkup(
            keyboard = _rows,
            isPersistent = isPersistent,
            resizeKeyboard = resizeKeyboard,
            oneTimeKeyboard = oneTimeKeyboard,
            inputFieldPlaceholder = inputFieldPlaceholder,
            selective = selective
        )
    }
}