package io.telegramkt.client

import kotlinx.serialization.json.Json

class TelegramParametersBuilder(val json: Json) {
    val params = mutableMapOf<String, Any?>()

    fun parameter(key: String, value: Any?) {
        params[key] = value
    }

    inline fun <reified T> jsonParameter(key: String, value: T?) {
        if (value != null) {
            params[key] = json.encodeToString(value)
        }
    }

    fun parameter(key: String, value: String?) {
        if (value != null) params[key] = value
    }

    fun parameter(key: String, value: Number?) {
        if (value != null) params[key] = value
    }

    fun parameter(key: String, value: Boolean?) {
        if (value != null) params[key] = value
    }
}