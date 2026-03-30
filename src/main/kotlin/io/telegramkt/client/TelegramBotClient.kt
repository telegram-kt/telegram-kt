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
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import io.telegramkt.api.TelegramApi
import io.telegramkt.api.TelegramResponse
import io.telegramkt.exception.TelegramApiException
import io.telegramkt.exception.TelegramNetworkException
import io.telegramkt.exception.TelegramRateLimitException
import io.telegramkt.json.TelegramJson
import io.telegramkt.model.ParseMode
import io.telegramkt.model.chat.ChatId
import io.telegramkt.model.file.File
import io.telegramkt.model.file.input.InputFile
import io.telegramkt.model.keyboard.reply.InlineKeyboardMarkup
import io.telegramkt.model.keyboard.reply.ReplyMarkup
import io.telegramkt.model.keyboard.reply.parameters.ReplyParameters
import io.telegramkt.model.location.Location
import io.telegramkt.model.location.Venue
import io.telegramkt.model.location.builder.LocationRequestBuilder
import io.telegramkt.model.location.builder.VenueRequestBuilder
import io.telegramkt.model.media.input.MediaGroupBuilder
import io.telegramkt.model.media.input.AlbumableMedia
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
        parameter("video_start_timestamp", videoStartTimestamp)
        parameter("caption", caption)
        parameter("parse_mode", parseMode?.name)
        parameter("caption_entities", captionEntities?.let { json.encodeToString(it) })
        parameter("show_caption_above_media", showCaptionAboveMedia)
        parameter("disable_notification", disableNotification)
        parameter("protect_content", protectContent)
        parameter("allow_paid_broadcast", allowPaidBroadcast)
        parameter("message_effect_id", messageEffectId)
        parameter("reply_parameters", replyParameters?.let { json.encodeToString(it) })
        parameter("reply_markup", replyMarkup?.let { json.encodeToString(it) })
    }

    override suspend fun copyMessages(
        chatId: ChatId,
        fromChatId: ChatId,
        messageIds: List<Int>,
        messageThreadId: Int?,
        directMessagesTopicId: Int?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        removeCaption: Boolean?
    ): List<MessageId> = call("copyMessages") {
        parameter("chat_id", chatId.toApiParam())
        parameter("from_chat_id", fromChatId.toApiParam())
        parameter("message_ids", messageIds)
        parameter("message_thread_id", messageThreadId)
        parameter("direct_messages_topic_id", directMessagesTopicId)
        parameter("disable_notification", disableNotification)
        parameter("protect_content", protectContent)
        parameter("remove_caption", removeCaption)
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

    override suspend fun getFile(fileId: String): File =
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
        chatId: ChatId,
        photo: InputFile,
        businessConnectionId: String?,
        messageThreadId: Int?,
        directMessagesTopicId: Int?,
        caption: String?,
        parseMode: ParseMode?,
        captionEntities: List<MessageEntity>?,
        showCaptionAboveMedia: Boolean?,
        hasSpoiler: Boolean?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        allowPaidBroadcast: Boolean?,
        messageEffectId: String?,
        suggestedPostParameters: SuggestedPostParameters?,
        replyParameters: ReplyParameters?,
        replyMarkup: ReplyMarkup?
    ): Message = call("sendPhoto") {
        parameter("chat_id", chatId.toApiParam())
        parameter("photo", photo)
        parameter("business_connection_id", businessConnectionId)
        parameter("message_thread_id", messageThreadId)
        parameter("direct_messages_topic_id", directMessagesTopicId)
        parameter("caption", caption)
        parameter("parse_mode", parseMode?.name)
        parameter("caption_entities", captionEntities?.let { json.encodeToString(it) })
        parameter("show_caption_above_media", showCaptionAboveMedia)
        parameter("has_spoiler", hasSpoiler)
        parameter("disable_notification", disableNotification)
        parameter("protect_content", protectContent)
        parameter("allow_paid_broadcast", allowPaidBroadcast)
        parameter("message_effect_id", messageEffectId)
        parameter("suggested_post_parameters", suggestedPostParameters?.let { json.encodeToString(it) })
        parameter("reply_parameters", replyParameters?.let { json.encodeToString(it) })
        parameter("reply_markup", replyMarkup?.let { json.encodeToString(it) })
    }

    override suspend fun sendAudio(
        chatId: ChatId,
        audio: InputFile,
        businessConnectionId: String?,
        messageThreadId: Int?,
        directMessagesTopicId: Int?,
        caption: String?,
        parseMode: ParseMode?,
        captionEntities: List<MessageEntity>?,
        duration: Int?,
        performer: String?,
        title: String?,
        thumbnail: InputFile?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        allowPaidBroadcast: Boolean?,
        messageEffectId: String?,
        suggestedPostParameters: SuggestedPostParameters?,
        replyParameters: ReplyParameters?,
        replyMarkup: ReplyMarkup?
    ): Message = call("sendAudio") {
        parameter("chat_id", chatId.toApiParam())
        parameter("audio", audio)
        parameter("business_connection_id", businessConnectionId)
        parameter("message_thread_id", messageThreadId)
        parameter("direct_messages_topic_id", directMessagesTopicId)
        parameter("caption", caption)
        parameter("parse_mode", parseMode?.name)
        parameter("caption_entities", captionEntities?.let { json.encodeToString(it) })
        parameter("duration", duration)
        parameter("performer", performer)
        parameter("title", title)
        parameter("thumbnail", thumbnail)
        parameter("disable_notification", disableNotification)
        parameter("protect_content", protectContent)
        parameter("allow_paid_broadcast", allowPaidBroadcast)
        parameter("message_effect_id", messageEffectId)
        parameter("suggested_post_parameters", suggestedPostParameters?.let { json.encodeToString(it) })
        parameter("reply_parameters", replyParameters?.let { json.encodeToString(it) })
        parameter("reply_markup", replyMarkup?.let { json.encodeToString(it) })
    }

    override suspend fun sendVoice(
        chatId: ChatId,
        voice: InputFile,
        businessConnectionId: String?,
        messageThreadId: Int?,
        directMessagesTopicId: Int?,
        caption: String?,
        parseMode: ParseMode?,
        captionEntities: List<MessageEntity>?,
        duration: Int?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        allowPaidBroadcast: Boolean?,
        messageEffectId: String?,
        suggestedPostParameters: SuggestedPostParameters?,
        replyParameters: ReplyParameters?,
        replyMarkup: ReplyMarkup?
    ): Message = call("sendVoice") {
        parameter("chat_id", chatId.toApiParam())
        parameter("voice", voice)
        parameter("business_connection_id", businessConnectionId)
        parameter("message_thread_id", messageThreadId)
        parameter("direct_messages_topic_id", directMessagesTopicId)
        parameter("caption", caption)
        parameter("parse_mode", parseMode?.name)
        parameter("caption_entities", captionEntities?.let { json.encodeToString(it) })
        parameter("duration", duration)
        parameter("disable_notification", disableNotification)
        parameter("protect_content", protectContent)
        parameter("allow_paid_broadcast", allowPaidBroadcast)
        parameter("message_effect_id", messageEffectId)
        parameter("suggested_post_parameters", suggestedPostParameters?.let { json.encodeToString(it) })
        parameter("reply_parameters", replyParameters?.let { json.encodeToString(it) })
        parameter("reply_markup", replyMarkup?.let { json.encodeToString(it) })
    }

    override suspend fun sendDocument(
        chatId: ChatId,
        document: InputFile,
        businessConnectionId: String?,
        messageThreadId: Int?,
        directMessagesTopicId: Int?,
        thumbnail: InputFile?,
        caption: String?,
        parseMode: ParseMode?,
        captionEntities: List<MessageEntity>?,
        disableContentTypeDetection: Boolean?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        allowPaidBroadcast: Boolean?,
        messageEffectId: String?,
        suggestedPostParameters: SuggestedPostParameters?,
        replyParameters: ReplyParameters?,
        replyMarkup: ReplyMarkup?
    ): Message  = call("sendDocument") {
        parameter("chat_id", chatId.toApiParam())
        parameter("document", document)
        parameter("business_connection_id", businessConnectionId)
        parameter("message_thread_id", messageThreadId)
        parameter("direct_messages_topic_id", directMessagesTopicId)
        parameter("thumbnail", thumbnail)
        parameter("caption", caption)
        parameter("parse_mode", parseMode?.name)
        parameter("caption_entities", captionEntities?.let { json.encodeToString(it) })
        parameter("disable_content_type_detection", disableContentTypeDetection)
        parameter("disable_notification", disableNotification)
        parameter("protect_content", protectContent)
        parameter("allow_paid_broadcast", allowPaidBroadcast)
        parameter("message_effect_id", messageEffectId)
        parameter("suggested_post_parameters", suggestedPostParameters?.let { json.encodeToString(it) })
        parameter("reply_parameters", replyParameters?.let { json.encodeToString(it) })
        parameter("reply_markup", replyMarkup?.let { json.encodeToString(it) })
    }

    override suspend fun sendVideo(
        chatId: ChatId,
        video: InputFile,
        businessConnectionId: String?,
        messageThreadId: Int?,
        directMessagesTopicId: Int?,
        duration: Int?,
        width: Int?,
        height: Int?,
        thumbnail: InputFile?,
        cover: InputFile?,
        startTimestamp: Int?,
        caption: String?,
        parseMode: ParseMode?,
        captionEntities: List<MessageEntity>?,
        showCaptionAboveMedia: Boolean?,
        hasSpoiler: Boolean?,
        supportsStreaming: Boolean?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        allowPaidBroadcast: Boolean?,
        messageEffectId: String?,
        suggestedPostParameters: SuggestedPostParameters?,
        replyParameters: ReplyParameters?,
        replyMarkup: ReplyMarkup?
    ): Message = call("sendVideo") {
        parameter("chat_id", chatId.toApiParam())
        parameter("video", video)
        parameter("business_connection_id", businessConnectionId)
        parameter("message_thread_id", messageThreadId)
        parameter("direct_messages_topic_id", directMessagesTopicId)
        parameter("duration", duration)
        parameter("width", width)
        parameter("height", height)
        parameter("thumbnail", thumbnail)
        parameter("cover", cover)
        parameter("start_timestamp", startTimestamp)
        parameter("caption", caption)
        parameter("parse_mode", parseMode?.name)
        parameter("caption_entities", captionEntities?.let { json.encodeToString(it) })
        parameter("show_caption_above_media", showCaptionAboveMedia)
        parameter("has_spoiler", hasSpoiler)
        parameter("supports_streaming", supportsStreaming)
        parameter("disable_notification", disableNotification)
        parameter("protect_content", protectContent)
        parameter("allow_paid_broadcast", allowPaidBroadcast)
        parameter("message_effect_id", messageEffectId)
        parameter("suggested_post_parameters", suggestedPostParameters?.let { json.encodeToString(it) })
        parameter("reply_parameters", replyParameters?.let { json.encodeToString(it) })
        parameter("reply_markup", replyMarkup?.let { json.encodeToString(it) })
    }

    override suspend fun sendVideoNote(
        chatId: ChatId,
        videoNote: InputFile,
        businessConnectionId: String?,
        messageThreadId: Int?,
        directMessagesTopicId: Int?,
        duration: Int?,
        length: Int?,
        thumbnail: InputFile?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        allowPaidBroadcast: Boolean?,
        messageEffectId: String?,
        suggestedPostParameters: SuggestedPostParameters?,
        replyParameters: ReplyParameters?,
        replyMarkup: ReplyMarkup?
    ): Message = call("sendVideoNote") {
        parameter("chat_id", chatId.toApiParam())
        parameter("video_note", videoNote)
        parameter("business_connection_id", businessConnectionId)
        parameter("message_thread_id", messageThreadId)
        parameter("direct_messages_topic_id", directMessagesTopicId)
        parameter("duration", duration)
        parameter("length", length)
        parameter("thumbnail", thumbnail)
        parameter("disable_notification", disableNotification)
        parameter("protect_content", protectContent)
        parameter("allow_paid_broadcast", allowPaidBroadcast)
        parameter("message_effect_id", messageEffectId)
        parameter("suggested_post_parameters", suggestedPostParameters?.let { json.encodeToString(it) })
        parameter("reply_parameters", replyParameters?.let { json.encodeToString(it) })
        parameter("reply_markup", replyMarkup?.let { json.encodeToString(it) })
    }

    override suspend fun sendAnimation(
        chatId: ChatId,
        animation: InputFile,
        businessConnectionId: String?,
        messageThreadId: Int?,
        directMessagesTopicId: Int?,
        duration: Int?,
        width: Int?,
        height: Int?,
        thumbnail: InputFile?,
        caption: String?,
        parseMode: ParseMode?,
        captionEntities: List<MessageEntity>?,
        showCaptionAboveMedia: Boolean?,
        hasSpoiler: Boolean?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        allowPaidBroadcast: Boolean?,
        messageEffectId: String?,
        suggestedPostParameters: SuggestedPostParameters?,
        replyParameters: ReplyParameters?,
        replyMarkup: ReplyMarkup?
    ): Message = call("sendAnimation") {
        parameter("chat_id", chatId.toApiParam())
        parameter("animation", animation)
        parameter("business_connection_id", businessConnectionId)
        parameter("message_thread_id", messageThreadId)
        parameter("direct_messages_topic_id", directMessagesTopicId)
        parameter("duration", duration)
        parameter("width", width)
        parameter("height", height)
        parameter("thumbnail", thumbnail)
        parameter("caption", caption)
        parameter("parse_mode", parseMode?.name)
        parameter("caption_entities", captionEntities?.let { json.encodeToString(it) })
        parameter("show_caption_above_media", showCaptionAboveMedia)
        parameter("has_spoiler", hasSpoiler)
        parameter("disable_notification", disableNotification)
        parameter("protect_content", protectContent)
        parameter("allow_paid_broadcast", allowPaidBroadcast)
        parameter("message_effect_id", messageEffectId)
        parameter("suggested_post_parameters", suggestedPostParameters?.let { json.encodeToString(it) })
        parameter("reply_parameters", replyParameters?.let { json.encodeToString(it) })
        parameter("reply_markup", replyMarkup?.let { json.encodeToString(it) })
    }

    override suspend fun sendMediaGroup(
        chatId: ChatId,
        media: List<AlbumableMedia>,
        businessConnectionId: String? ,
        messageThreadId: Int?,
        directMessagesTopicId: Int?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        allowPaidBroadcast: Boolean?,
        messageEffectId: String?,
        replyParameters: ReplyParameters?,
    ): List<Message> = call("sendMediaGroup") {
        parameter("chat_id", chatId.toApiParam())
        parameter("media", media)
        parameter("business_connection_id", businessConnectionId)
        parameter("message_thread_id", messageThreadId)
        parameter("direct_messages_topic_id", directMessagesTopicId)
        parameter("disable_notification", disableNotification)
        parameter("protect_content", protectContent)
        parameter("allow_paid_broadcast", allowPaidBroadcast)
        parameter("message_effect_id", messageEffectId)
        parameter("reply_parameters", replyParameters?.let { json.encodeToString(it) })
    }

    suspend fun sendMediaGroup(
        chatId: ChatId,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        replyParameters: ReplyParameters? = null,
        block: MediaGroupBuilder.PhotoVideo.() -> Unit
    ): List<Message> {
        val builder = MediaGroupBuilder.PhotoVideo().apply(block)
        return sendMediaGroup(chatId, builder.media, businessConnectionId, messageThreadId, directMessagesTopicId,
            disableNotification, protectContent, allowPaidBroadcast, messageEffectId, replyParameters)
    }

    suspend fun sendDocumentsGroup(
        chatId: ChatId,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        replyParameters: ReplyParameters? = null,
        block: MediaGroupBuilder.Documents.() -> Unit,
    ): List<Message> {
        val builder = MediaGroupBuilder.Documents().apply(block)
        return sendMediaGroup(chatId, builder.media, businessConnectionId, messageThreadId, directMessagesTopicId,
            disableNotification, protectContent, allowPaidBroadcast, messageEffectId, replyParameters)
    }

    suspend fun sendAudioGroup(
        chatId: ChatId,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        replyParameters: ReplyParameters? = null,
        block: MediaGroupBuilder.Audio.() -> Unit,
    ): List<Message> {
        val builder = MediaGroupBuilder.Audio().apply(block)
        return sendMediaGroup(chatId, builder.media, businessConnectionId, messageThreadId, directMessagesTopicId,
            disableNotification, protectContent, allowPaidBroadcast, messageEffectId, replyParameters)
    }

    // ===== Location methods. =====

    override suspend fun sendLocation(
        chatId: ChatId,
        latitude: Float,
        longitude: Float,
        businessConnectionId: String?,
        messageThreadId: Int?,
        directMessagesTopicId: Int?,
        horizontalAccuracy: Float?,
        livePeriod: Int?,
        heading: Int?,
        proximityAlertRadius: Int?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        allowPaidBroadcast: Boolean?,
        messageEffectId: String?,
        suggestedPostParameters: SuggestedPostParameters?,
        replyParameters: ReplyParameters?,
        replyMarkup: ReplyMarkup?
    ): Message = call("sendLocation") {
        parameter("chat_id", chatId.toApiParam())
        parameter("latitude", latitude)
        parameter("longitude", longitude)
        parameter("business_connection_id", businessConnectionId)
        parameter("message_thread_id", messageThreadId)
        parameter("direct_messages_topic_id", directMessagesTopicId)
        parameter("horizontal_accuracy", horizontalAccuracy)
        parameter("live_period", livePeriod)
        parameter("disable_notification", disableNotification)
        parameter("protect_content", protectContent)
        parameter("allow_paid_broadcast", allowPaidBroadcast)
        parameter("message_effect_id", messageEffectId)
        parameter("reply_parameters", replyParameters?.let { json.encodeToString(it) })
        parameter("suggested_post_parameters", suggestedPostParameters?.let { json.encodeToString(it) })
        parameter("reply_parameters", replyParameters?.let { json.encodeToString(it) })
        parameter("reply_markup", replyMarkup?.let { json.encodeToString(it) })
    }

    /**
     * Send location using [Location] object.
     * Delegates to primary method with individual parameters.
     */
    suspend fun sendLocation(
        chatId: ChatId,
        location: Location,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        suggestedPostParameters: SuggestedPostParameters? = null,
        replyParameters: ReplyParameters? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Message = sendLocation(
        chatId = chatId,
        latitude = location.latitude,
        longitude = location.longitude,
        horizontalAccuracy = location.horizontalAccuracy,
        livePeriod = location.livePeriod,
        heading = location.heading,
        proximityAlertRadius = location.proximityAlertRadius,
        businessConnectionId = businessConnectionId,
        messageThreadId = messageThreadId,
        directMessagesTopicId = directMessagesTopicId,
        disableNotification = disableNotification,
        protectContent = protectContent,
        allowPaidBroadcast = allowPaidBroadcast,
        messageEffectId = messageEffectId,
        suggestedPostParameters = suggestedPostParameters,
        replyParameters = replyParameters,
        replyMarkup = replyMarkup
    )

    /**
     * Send location with DSL builder for optional parameters.
     * Most convenient for Java-style callers.
     */
    suspend fun sendLocation(
        chatId: ChatId,
        latitude: Float,
        longitude: Float,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        suggestedPostParameters: SuggestedPostParameters? = null,
        replyParameters: ReplyParameters? = null,
        replyMarkup: ReplyMarkup? = null,
        block: LocationRequestBuilder.() -> Unit = {}
    ): Message {
        val builder = LocationRequestBuilder(latitude, longitude).apply(block)
        return sendLocation(chatId, builder.build(), businessConnectionId, messageThreadId, directMessagesTopicId,
            disableNotification, protectContent, allowPaidBroadcast, messageEffectId, suggestedPostParameters,
            replyParameters, replyMarkup)
    }

    // ===== Venue methods. =====

    override suspend fun sendVenue(
        chatId: ChatId,
        latitude: Float,
        longitude: Float,
        title: String,
        address: String,
        foursquareId: String?,
        foursquareType: String?,
        googlePlaceId: String?,
        googlePlaceType: String?,
        businessConnectionId: String?,
        messageThreadId: Int?,
        directMessagesTopicId: Int?,
        horizontalAccuracy: Float?,
        livePeriod: Int?,
        heading: Int?,
        proximityAlertRadius: Int?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        allowPaidBroadcast: Boolean?,
        messageEffectId: String?,
        suggestedPostParameters: SuggestedPostParameters?,
        replyParameters: ReplyParameters?,
        replyMarkup: ReplyMarkup?
    ): Message = call("sendVenue") {
        parameter("chat_id", chatId.toApiParam())
        parameter("latitude", latitude)
        parameter("longitude", longitude)
        parameter("title", title)
        parameter("address", address)
        parameter("foursquare_id", foursquareId)
        parameter("foursquare_type", foursquareType)
        parameter("google_place_id", googlePlaceId)
        parameter("google_place_type", googlePlaceType)
        parameter("business_connection_id", businessConnectionId)
        parameter("message_thread_id", messageThreadId)
        parameter("direct_messages_topic_id", directMessagesTopicId)
        parameter("horizontal_accuracy", horizontalAccuracy)
        parameter("live_period", livePeriod)
        parameter("disable_notification", disableNotification)
        parameter("protect_content", protectContent)
        parameter("allow_paid_broadcast", allowPaidBroadcast)
        parameter("message_effect_id", messageEffectId)
        parameter("reply_parameters", replyParameters?.let { json.encodeToString(it) })
        parameter("suggested_post_parameters", suggestedPostParameters?.let { json.encodeToString(it) })
        parameter("reply_parameters", replyParameters?.let { json.encodeToString(it) })
        parameter("reply_markup", replyMarkup?.let { json.encodeToString(it) })
    }

    /**
     * Send venue using [Venue] object.
     * Delegates to primary method with individual parameters.
     */
    suspend fun sendVenue(
        chatId: ChatId,
        venue: Venue,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        suggestedPostParameters: SuggestedPostParameters? = null,
        replyParameters: ReplyParameters? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Message = sendVenue(
        chatId = chatId,
        latitude = venue.location.latitude,
        longitude = venue.location.longitude,
        title = venue.title,
        address = venue.address,
        foursquareId = venue.foursquareId,
        foursquareType = venue.foursquareType,
        googlePlaceId = venue.googlePlaceId,
        googlePlaceType = venue.googlePlaceType,
        horizontalAccuracy = venue.location.horizontalAccuracy,
        livePeriod = venue.location.livePeriod,
        heading = venue.location.heading,
        proximityAlertRadius = venue.location.proximityAlertRadius,
        businessConnectionId = businessConnectionId,
        messageThreadId = messageThreadId,
        directMessagesTopicId = directMessagesTopicId,
        disableNotification = disableNotification,
        protectContent = protectContent,
        allowPaidBroadcast = allowPaidBroadcast,
        messageEffectId = messageEffectId,
        suggestedPostParameters = suggestedPostParameters,
        replyParameters = replyParameters,
        replyMarkup = replyMarkup
    )

    /**
     * Send venue using [Location] object with title and address.
     * Useful when you already have a Location instance.
     */
    suspend fun sendVenue(
        chatId: ChatId,
        location: Location,
        title: String,
        address: String,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        suggestedPostParameters: SuggestedPostParameters? = null,
        replyParameters: ReplyParameters? = null,
        replyMarkup: ReplyMarkup? = null,
        block: VenueRequestBuilder.() -> Unit = {}
    ): Message {
        val builder = VenueRequestBuilder(location, title, address).apply(block)
        return sendVenue(chatId, builder.build(), businessConnectionId, messageThreadId, directMessagesTopicId,
            disableNotification, protectContent, allowPaidBroadcast, messageEffectId, suggestedPostParameters, replyParameters,
            replyMarkup)
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