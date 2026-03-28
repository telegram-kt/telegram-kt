package io.telegramkt.client

import kotlinx.serialization.json.Json

class TelegramParametersBuilder(private val json: Json) {
    val params = mutableMapOf<String, Any?>()

    fun parameter(key: String, value: Any?) {
        params[key] = value
    }

    fun jsonParameter(key: String, value: Any?) {
        params[key] = value?.let { json.encodeToString(it) }
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