package io.telegramkt.model.bot.command

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class BotCommandScope {

    @Serializable
    @SerialName("default")
    data object Default : BotCommandScope()

    @Serializable
    @SerialName("all_private_chats")
    data object AllPrivateChats : BotCommandScope()

    @Serializable
    @SerialName("all_group_chats")
    data object AllGroupChats : BotCommandScope()

    @Serializable
    @SerialName("all_chat_administrators")
    data object AllChatAdministrators : BotCommandScope()

    @Serializable
    @SerialName("chat")
    data class Chat(
        @SerialName("chat_id") val chatId: Long
    ) : BotCommandScope()

    @Serializable
    @SerialName("chat_administrators")
    data class ChatAdministrators(
        @SerialName("chat_id") val chatId: Long
    ) : BotCommandScope()

    @Serializable
    @SerialName("chat_member")
    data class ChatMember(
        @SerialName("chat_id") val chatId: Long,
        @SerialName("user_id") val userId: Long
    ) : BotCommandScope()
}