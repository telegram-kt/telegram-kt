package io.telegramkt.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.telegramkt.api.TelegramApi
import io.telegramkt.api.TelegramResponse
import io.telegramkt.exception.TelegramApiException
import io.telegramkt.exception.TelegramNetworkException
import io.telegramkt.exception.TelegramRateLimitException
import io.telegramkt.json.TelegramJson
import io.telegramkt.model.ParseMode
import io.telegramkt.model.chat.ChatId
import io.telegramkt.model.file.input.InputFile
import io.telegramkt.model.keyboard.reply.InlineKeyboardMarkup
import io.telegramkt.model.keyboard.reply.ReplyMarkup
import io.telegramkt.model.keyboard.reply.parameters.ReplyParameters
import io.telegramkt.model.message.Message
import io.telegramkt.model.message.MessageId
import io.telegramkt.model.message.entity.MessageEntity
import io.telegramkt.model.suggested.SuggestedPostParameters
import io.telegramkt.model.update.Update
import io.telegramkt.model.user.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import java.lang.AutoCloseable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TelegramBotClient(
    private val token: String,
    private val apiUrl: String = "https://api.telegram.org",
    private val timeout: Duration = 30.seconds,
    private val json: Json = TelegramJson
) : TelegramApi, AutoCloseable {

    private val httpClient = HttpClient(CIO) {
        expectSuccess = true
        engine {
            endpoint {
                connectTimeout = timeout.inWholeMilliseconds.toLong()
                requestTimeout = timeout.inWholeMilliseconds.toLong()
            }
        }
        install(ContentNegotiation) {
            json(json)
        }
    }

    private val baseUrl: String get() = "$apiUrl/bot$token"

    // ===== API METHODS. =====

    override suspend fun getMe(): User = call("getMe")

    override suspend fun sendMessage(
        chatId: ChatId,
        text: String,
        parseMode: ParseMode?,
        entities: List<MessageEntity>?,
        disableWebPagePreview: Boolean?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        replyToMessageId: Int?,
        allowSendingWithoutReply: Boolean?,
        replyMarkup: ReplyMarkup?,
        businessConnectionId: String?,
        messageEffectId: String?,
    ): Message = call("sendMessage") {
        parameter("chat_id", chatId.toApiParam())
        parameter("text", text)
        parameter("parse_mode", parseMode?.name)
        parameter("entities", entities?.let { json.encodeToString(it) })
        parameter("disable_web_page_preview", disableWebPagePreview)
        parameter("disable_notification", disableNotification)
        parameter("protect_content", protectContent)
        parameter("reply_to_message_id", replyToMessageId)
        parameter("allow_sending_without_reply", allowSendingWithoutReply)
        parameter("reply_markup", replyMarkup?.let { json.encodeToString(it) })
        parameter("business_connection_id", businessConnectionId)
        parameter("message_effect_id", messageEffectId)
    }

    override suspend fun editMessageText(
        chatId: ChatId?,
        messageId: Int?,
        inlineMessageId: String?,
        text: String,
        parseMode: ParseMode?,
        entities: List<MessageEntity>?,
        disableWebPagePreview: Boolean?,
        replyMarkup: InlineKeyboardMarkup?,
        businessConnectionId: String?,
    ): Message = call("editMessageText") {
        parameter("chat_id", chatId?.toApiParam())
        parameter("message_id", messageId)
        parameter("inline_message_id", inlineMessageId)
        parameter("text", text)
        parameter("parse_mode", parseMode?.name)
        parameter("entities", entities?.let { json.encodeToString(it) })
        parameter("disable_web_page_preview", disableWebPagePreview)
        parameter("reply_markup", replyMarkup?.let { json.encodeToString(it) })
        parameter("business_connection_id", businessConnectionId)
    }

    override suspend fun deleteMessage(chatId: ChatId, messageId: Int): Boolean = call("deleteMessage") {
        parameter("chat_id", chatId.toApiParam())
        parameter("message_id", messageId)
    }

    override suspend fun deleteMessages(
        chatId: ChatId,
        messageIds: List<Int>
    ): Boolean {
        require(messageIds.isNotEmpty()) { "messageIds must not be empty" }
        require(messageIds.size <= 100) { "messageIds must not exceed 100 items, got ${messageIds.size}" }

        return call<Boolean>("deleteMessages") {
            parameter("chat_id", chatId.toApiParam())
            parameter("message_ids", messageIds)
        }
    }

    override suspend fun forwardMessage(
        chatId: ChatId,
        fromChatId: ChatId,
        messageId: Int,
        messageThreadId: Int?,
        directMessagesTopicId: Int?,
        videoStartTimestamp: Int?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        messageEffectId: String?,
        suggestedPostParameters: SuggestedPostParameters?
    ): Message = call("forwardMessage") {
        parameter("chat_id", chatId.toApiParam())
        parameter("from_chat_id", fromChatId.toApiParam())
        parameter("message_id", messageId)
        parameter("message_thread_id", messageThreadId)
        parameter("direct_messages_topic_id", directMessagesTopicId)
        parameter("video_start_timestamp", videoStartTimestamp)
        parameter("disable_notification", disableNotification)
        parameter("protect_content", protectContent)
        parameter("message_effect_id", messageEffectId)
        parameter("suggested_post_parameters", suggestedPostParameters)
    }

    override suspend fun forwardMessages(
        chatId: ChatId,
        fromChatId: ChatId,
        messageIds: List<Int>,
        messageThreadId: Int?,
        directMessagesTopicId: Int?,
        disableNotification: Boolean?,
        protectContent: Boolean?
    ): Message {
        require(messageIds.isNotEmpty()) { "messageIds must not be empty" }
        require(messageIds.size <= 100) { "messageIds must not exceed 100 items, got ${messageIds.size}" }

        return call("forwardMessages") {
            parameter("chat_id", chatId.toApiParam())
            parameter("from_chat_id", fromChatId.toApiParam())
            parameter("message_ids", messageIds)
            parameter("message_thread_id", messageThreadId)
            parameter("direct_messages_topic_id", directMessagesTopicId)
            parameter("disable_notification", disableNotification)
            parameter("protect_content", protectContent)
        }
    }

    override suspend fun copyMessage(
        chatId: ChatId,
        fromChatId: ChatId,
        messageId: Int,
        messageThreadId: Int?,
        directMessagesTopicId: Int?,
        videoStartTimestamp: Int?,
        caption: String?,
        parseMode: ParseMode?,
        captionEntities: List<MessageEntity>?,
        showCaptionAboveMedia: Boolean?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        allowPaidBroadcast: Boolean?,
        messageEffectId: String?,
        replyParameters: ReplyParameters?,
        replyMarkup: ReplyMarkup?
    ): MessageId = call("copyMessage") {
        parameter("chat_id", chatId.toApiParam())
        parameter("from_chat_id", fromChatId.toApiParam())
        parameter("message_id", messageId)
        parameter("message_thread_id", messageThreadId)
        parameter("direct_messages_topic_id", directMessagesTopicId)
        parameter("video_start_timestamp", directMessagesTopicId)
        parameter("caption", caption)
        parameter("parse_mode", parseMode)
        parameter("caption_entities", captionEntities)
        parameter("show_caption_above_media", showCaptionAboveMedia)
        parameter("disable_notification", disableNotification)
        parameter("protect_content", protectContent)
        parameter("allow_paid_broadcast", allowPaidBroadcast)
        parameter("message_effect_id", messageEffectId)
        parameter("reply_parameters", replyParameters)
        parameter("reply_markup", replyMarkup)
    }

    override suspend fun getUpdates(
        offset: Int?,
        limit: Int?,
        timeout: Int?,
        allowedUpdates: List<String>?,
    ): List<Update> = call("getUpdates") {
        parameter("offset", offset)
        parameter("limit", limit)
        parameter("timeout", timeout)
        parameter("allowed_updates", allowedUpdates?.let { json.encodeToString(it) })
    }

    override suspend fun getFile(fileId: String): io.telegramkt.model.file.File =
        call("getFile") { parameter("file_id", fileId) }

    override suspend fun answerCallbackQuery(
        callbackQueryId: String,
        text: String?,
        showAlert: Boolean,
        url: String?,
        cacheTime: Int?,
    ) {
        call<Unit>("answerCallbackQuery") {
            parameter("callback_query_id", callbackQueryId)
            parameter("text", text)
            parameter("show_alert", showAlert)
            parameter("url", url)
            parameter("cache_time", cacheTime)
        }
    }

    override suspend fun sendPhoto(
        businessConnectionId: String?,
        chatId: ChatId,
        messageThreadId: Int?,
        directMessagesTopicId: Int?,
        photo: InputFile,
        caption: String?,
        parseMode: ParseMode?,
        captionEntities: List<MessageEntity>?,
        showCaptionAboveMedia: Boolean?,
        hasSpoiler: Boolean?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        allowPaidBroadcast: Boolean?,
        messageEffectId: String?,
        replyMarkup: ReplyMarkup?
    ): Message = call("sendPhoto") {
        parameter("business_connection_id", businessConnectionId)
        parameter("chat_id", chatId.toApiParam())
        parameter("message_thread_id", messageThreadId)
        parameter("direct_messages_topic_id", directMessagesTopicId)
        parameter("photo", photo)
        parameter("caption", caption)
        parameter("parse_mode", parseMode?.name)
        parameter("caption_entities", captionEntities?.let { json.encodeToString(it) })
        parameter("show_caption_above_media", showCaptionAboveMedia)
        parameter("has_spoiler", hasSpoiler)
        parameter("disable_notification", disableNotification)
        parameter("protect_content", protectContent)
        parameter("allow_paid_broadcast", allowPaidBroadcast)
        parameter("message_effect_id", messageEffectId)
        parameter("reply_markup", replyMarkup?.let { json.encodeToString(it) })
    }

    fun updatesFlow(
        limit: Int = 100,
        timeout: Int = 30,
        allowedUpdates: List<String>? = null,
    ): Flow<Update> = flow {
        var offset: Int? = null
        while (true) {
            try {
                val updates = getUpdates(
                    offset = offset,
                    limit = limit,
                    timeout = timeout,
                    allowedUpdates = allowedUpdates,
                )
                for (update in updates) {
                    emit(update)
                    offset = update.updateId + 1
                }
                if (updates.isEmpty()) {
                    delay(500)
                }
            } catch (e: TelegramRateLimitException) {
                e.parameters?.retryAfter?.let { retryAfter ->
                    delay(retryAfter * 1000L)
                } ?: delay(5000)
            } catch (e: TelegramApiException) {
                println("API Error: ${e.message}")
                delay(1000)
            } catch (e: Exception) {
                println("Network Error: ${e.message}")
                delay(1000)
            }
        }
    }

    // ===== Call Method. =====

    private suspend inline fun <reified T> call(
        method: String,
        crossinline block: TelegramParametersBuilder.() -> Unit = {}
    ): T {
        val builder = TelegramParametersBuilder(json).apply(block)
        val params = builder.params

        val response = try {
            httpClient.post("$baseUrl/$method") {
                if (params.hasBinaryFiles()) {
                    contentType(ContentType.MultiPart.FormData)
                    setBody(json.buildMultipartBody(params))
                } else {
                    contentType(ContentType.Application.Json)
                    setBody(json.buildJsonBody(params))
                }
            }.body<TelegramResponse<T>>()
        } catch (e: Exception) {
            throw TelegramNetworkException("Failed to call $method", e)
        }

        return response.requireSuccess().requireResult()
    }

    override fun close() = httpClient.close()
}