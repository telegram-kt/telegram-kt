package io.telegramkt.json

import kotlinx.serialization.json.Json

val TelegramJson: Json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = false
    explicitNulls = false
    prettyPrint = false
}