package io.telegramkt.model.bot.command

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class BotCommandScope {
    abstract val type: String

    @Serializable
    @SerialName("default")
    data object Default : BotCommandScope() {
        override val type = "default"
    }

    @Serializable
    @SerialName("all_private_chats")
    data object AllPrivateChats : BotCommandScope() {
        override val type = "all_private_chats"
    }

    @Serializable
    @SerialName("all_group_chats")
    data object AllGroupChats : BotCommandScope() {
        override val type = "all_group_chats"
    }

    @Serializable
    @SerialName("all_chat_administrators")
    data object AllChatAdministrators : BotCommandScope() {
        override val type = "all_chat_administrators"
    }

    @Serializable
    @SerialName("chat")
    data class Chat(
        @SerialName("chat_id") val chatId: Long
    ) : BotCommandScope() {
        override val type = "chat"
    }

    @Serializable
    @SerialName("chat_administrators")
    data class ChatAdministrators(
        @SerialName("chat_id") val chatId: Long
    ) : BotCommandScope() {
        override val type = "chat_administrators"
    }

    @Serializable
    @SerialName("chat_member")
    data class ChatMember(
        @SerialName("chat_id") val chatId: Long,
        @SerialName("user_id") val userId: Long
    ) : BotCommandScope() {
        override val type = "chat_member"
    }
}