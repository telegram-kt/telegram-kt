package io.telegramkt.exception

import io.telegramkt.api.ResponseParameters

open class TelegramApiException (
    val errorCode: Int,
    message: String,
    val parameters: ResponseParameters? = null,
) : Exception("Telegram API Error [$errorCode]: $message")