package io.telegramkt.model.keyboard.reply.parameters

import io.telegramkt.model.ParseMode
import io.telegramkt.model.chat.ChatId
import io.telegramkt.model.message.entity.MessageEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReplyParameters(
    @SerialName("message_id") val messageId: Int,
    @SerialName("chat_id") val chatId: ChatId,
    @SerialName("allow_sending_without_reply") val allowSendingWithoutReply: Boolean? = null,
    @SerialName("quote") val quote: String? = null,
    @SerialName("quote_parse_mode") val quoteParseMode: ParseMode? = null,
    @SerialName("quote_entities") val quoteEntities: List<MessageEntity>? = null,
    @SerialName("checklist_task_id") val checklistTaskId: Int? = null,
)
