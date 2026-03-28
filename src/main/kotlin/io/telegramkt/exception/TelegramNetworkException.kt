package io.telegramkt.exception

class TelegramNetworkException(
    message: String,
    cause: Throwable? = null,
) : Exception("Telegram Network Error: $message", cause)