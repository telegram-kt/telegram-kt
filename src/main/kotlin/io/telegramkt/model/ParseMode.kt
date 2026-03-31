package io.telegramkt.model

import io.telegramkt.serialization.ParseModeSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ParseModeSerializer::class)
enum class ParseMode {
    MARKDOWN,
    HTML,
    MARKDOWN_V2,
}