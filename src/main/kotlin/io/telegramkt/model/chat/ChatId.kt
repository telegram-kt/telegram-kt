package io.telegramkt.model.chat

import kotlinx.serialization.Serializable

@Serializable
sealed class ChatId {
    data class ById(val id: Long) : ChatId()
    data class ByUsername(val username: String) : ChatId()

    fun toApiParam(): String = when (this) {
        is ById -> id.toString()
        is ByUsername -> if (username.startsWith("@")) username else "@$username"
    }
}