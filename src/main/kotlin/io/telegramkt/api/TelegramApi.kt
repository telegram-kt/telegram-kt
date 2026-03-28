package io.telegramkt.api

import io.telegramkt.model.ParseMode
import io.telegramkt.model.chat.ChatId
import io.telegramkt.model.file.File
import io.telegramkt.model.file.input.InputFile
import io.telegramkt.model.keyboard.reply.InlineKeyboardMarkup
import io.telegramkt.model.keyboard.reply.ReplyMarkup
import io.telegramkt.model.message.Message
import io.telegramkt.model.message.entity.MessageEntity
import io.telegramkt.model.update.Update
import io.telegramkt.model.user.User

interface TelegramApi {
    suspend fun getMe(): User

    suspend fun getUpdates(
        offset: Int? = null,
        limit: Int? = null,
        timeout: Int? = null,
        allowedUpdates: List<String>? = null,
    ): List<Update>

    suspend fun sendMessage(
        chatId: ChatId,
        text: String,
        parseMode: ParseMode? = null,
        entities: List<MessageEntity>? = null,
        disableWebPagePreview: Boolean? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        replyToMessageId: Int? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
        businessConnectionId: String? = null,
        messageEffectId: String? = null,
    ): Message

    suspend fun editMessageText(
        chatId: ChatId? = null,
        messageId: Int? = null,
        inlineMessageId: String? = null,
        text: String,
        parseMode: ParseMode? = null,
        entities: List<MessageEntity>? = null,
        disableWebPagePreview: Boolean? = null,
        replyMarkup: InlineKeyboardMarkup? = null,
        businessConnectionId: String? = null,
    ): Message

    suspend fun getFile(fileId: String) : File

    suspend fun answerCallbackQuery(
        callbackQueryId: String,
        text: String? = null,
        showAlert: Boolean = false,
        url: String? = null,
        cacheTime: Int? = null,
    )

    suspend fun sendPhoto(
        businessConnectionId: String? = null,
        chatId: ChatId,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        photo: InputFile,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        showCaptionAboveMedia: Boolean? = null,
        hasSpoiler: Boolean? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        // suggestedPostParameters: SuggestedPostParameters? = null,
        // replyParameters: ReplyParameters? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Message
}