package io.telegramkt.exception

class TelegramRateLimitException(
    retryAfter: Int,
) : TelegramApiException(429, "Rate limit exceeded. Retry after $retryAfter seconds")