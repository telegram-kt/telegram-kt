package io.telegramkt.model.keyboard.inline

import io.telegramkt.model.keyboard.button.CopyTextButton
import io.telegramkt.model.web.LoginUrlInfo
import io.telegramkt.model.web.WebAppInfo

class InlineKeyboardBuilder {
    private val _rows = mutableListOf<MutableList<InlineKeyboardButton>>()
    private var currentRow: MutableList<InlineKeyboardButton>? = null

    val rows: List<List<InlineKeyboardButton>> get() = _rows

    fun row(vararg buttons: InlineKeyboardButton) {
        if (currentRow != null && currentRow!!.isNotEmpty()) {
            _rows.add(currentRow!!)
        }
        currentRow = buttons.toMutableList()
    }

    private fun addToCurrentRow(button: InlineKeyboardButton) {
        if (currentRow == null) {
            currentRow = mutableListOf()
        }
        currentRow!!.add(button)
    }

    fun endRow() {
        currentRow?.let {
            if (it.isNotEmpty()) _rows.add(it)
        }
        currentRow = null
    }

    fun url(text: String, url: String): InlineKeyboardButton.Url =
        InlineKeyboardButton.Url(text, url).also { addToCurrentRow(it) }

    fun callback(text: String, callbackData: String): InlineKeyboardButton.Callback =
        InlineKeyboardButton.Callback(text, callbackData).also { addToCurrentRow(it) }

    fun webApp(text: String, webApp: WebAppInfo): InlineKeyboardButton.WebApp =
    InlineKeyboardButton.WebApp(text, webApp).also { addToCurrentRow(it) }

    fun loginUrl(text: String, loginUrl: LoginUrlInfo): InlineKeyboardButton.LoginUrl =
    InlineKeyboardButton.LoginUrl(text, loginUrl).also { addToCurrentRow(it) }

    fun switchInlineQuery(text: String, query: String): InlineKeyboardButton.SwitchInlineQuery =
        InlineKeyboardButton.SwitchInlineQuery(text, query).also { addToCurrentRow(it) }

    fun switchInlineQueryCurrentChat(text: String, query: String): InlineKeyboardButton.SwitchInlineQueryCurrentChat =
        InlineKeyboardButton.SwitchInlineQueryCurrentChat(text, query).also { addToCurrentRow(it) }

    fun copyText(text: String, copyText: CopyTextButton): InlineKeyboardButton.CopyText =
    InlineKeyboardButton.CopyText(text, copyText).also { addToCurrentRow(it) }

    fun pay(text: String): InlineKeyboardButton.Pay =
        InlineKeyboardButton.Pay(text).also { addToCurrentRow(it) }
}