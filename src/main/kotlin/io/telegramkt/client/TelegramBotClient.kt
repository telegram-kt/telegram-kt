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
import io.telegramkt.model.chat.action.ChatAction
import io.telegramkt.model.chat.administrator.ChatPermissions
import io.telegramkt.model.chat.administrator.ChatPermissionsBuilder
import io.telegramkt.model.checklist.input.InputChecklist
import io.telegramkt.model.contact.Contact
import io.telegramkt.model.contact.ContactRequestBuilder
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
import io.telegramkt.model.poll.PollType
import io.telegramkt.model.poll.input.InputPollOption
import io.telegramkt.model.poll.input.PollOptionsBuilder
import io.telegramkt.model.reaction.ReactionBuilder
import io.telegramkt.model.reaction.ReactionType
import io.telegramkt.model.suggested.SuggestedPostParameters
import io.telegramkt.model.update.Update
import io.telegramkt.model.user.User
import io.telegramkt.model.user.profile.UserProfileAudios
import io.telegramkt.model.user.profile.UserProfilePhotos
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import java.lang.AutoCloseable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class TelegramBotClient(
    internal val token: String,
    internal val apiUrl: String = "https://api.telegram.org",
    private val timeout: Duration = 30.seconds,
    private val json: Json = TelegramJson
) : TelegramApi, AutoCloseable {

    internal val httpClient = HttpClient(CIO) {
        expectSuccess = true
        engine {
            endpoint {
                connectTimeout = timeout.inWholeMilliseconds
                requestTimeout = timeout.inWholeMilliseconds
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
        parameter("parse_mode", parseMode)
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

    override suspend fun sendMessageDraft(
        chatId: Int,
        draftId: Int,
        text: String,
        messageThreadId: Int?,
        parseMode: ParseMode?,
        entities: List<MessageEntity>?
    ): Boolean {
        require(draftId != 0) { "draftId cannot be zero" }
        require(text.length in 1..4096) { "The text must be between 1 and 4096 characters." }

        return call("sendMessageDraft") {
            parameter("chat_id", chatId)
            parameter("draft_id", draftId)
            parameter("text", text)
            parameter("message_thread_id", messageThreadId)
            parameter("parse_mode", parseMode)
            parameter("entities", entities)
        }
    }

    override suspend fun editMessageText(
        chatId: ChatId?,
        messageId: Int?,
        text: String,
        inlineMessageId: String?,
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
        parameter("parse_mode", parseMode)
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
        parameter("parse_mode", parseMode)
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
        parameter("parse_mode", parseMode)
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
        parameter("parse_mode", parseMode)
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
        parameter("parse_mode", parseMode)
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
        parameter("parse_mode", parseMode)
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
        parameter("parse_mode", parseMode)
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
        parameter("parse_mode", parseMode)
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

    // ===== Contact methods. =====
    override suspend fun sendContact(
        chatId: ChatId,
        phoneNumber: String,
        firstName: String,
        lastName: String?,
        vCard: String?,
        businessConnectionId: String?,
        messageThreadId: Int?,
        directMessagesTopicId: Int?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        allowPaidBroadcast: Boolean?,
        messageEffectId: String?,
        suggestedPostParameters: SuggestedPostParameters?,
        replyParameters: ReplyParameters?,
        replyMarkup: ReplyMarkup?
    ): Message = call("sendContact") {
        parameter("chat_id", chatId.toApiParam())
        parameter("phone_number", phoneNumber)
        parameter("first_name", firstName)
        parameter("last_name", lastName)
        parameter("vcard", vCard)
        parameter("business_connection_id", businessConnectionId)
        parameter("message_thread_id", messageThreadId)
        parameter("direct_messages_topic_id", directMessagesTopicId)
        parameter("disable_notification", disableNotification)
        parameter("protect_content", protectContent)
        parameter("allow_paid_broadcast", allowPaidBroadcast)
        parameter("message_effect_id", messageEffectId)
        parameter("suggested_post_parameters", suggestedPostParameters?.let { json.encodeToString(it) })
        parameter("reply_parameters", replyParameters?.let { json.encodeToString(it) })
        parameter("reply_markup", replyMarkup?.let { json.encodeToString(it) })
    }

    suspend fun sendContact(
        chatId: ChatId,
        contact: Contact,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        suggestedPostParameters: SuggestedPostParameters? = null,
        replyParameters: ReplyParameters? = null,
        replyMarkup: ReplyMarkup? = null
    ): Message = sendContact(chatId, contact.phoneNumber, contact.firstName, contact.lastName, contact.vCard,
        businessConnectionId, messageThreadId, directMessagesTopicId, disableNotification, protectContent,
        allowPaidBroadcast, messageEffectId, suggestedPostParameters, replyParameters, replyMarkup)

    /**
     * Send contact with DSL builder for options.
     *
     * Example:
     * ```
     * client.sendContact(chatId, "+1-555-123-4567", "John") {
     *     lastName = "Doe"
     * }
     * ```
     */
    suspend fun sendContact(
        chatId: ChatId,
        phoneNumber: String,
        firstName: String,
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
        block: ContactRequestBuilder.() -> Unit = {}
    ): Message {
        val builder = ContactRequestBuilder(phoneNumber, firstName).apply(block)
        return sendContact(chatId, builder.build(), businessConnectionId, messageThreadId, directMessagesTopicId,
            disableNotification, protectContent, allowPaidBroadcast, messageEffectId, suggestedPostParameters,
            replyParameters, replyMarkup)
    }

    // ===== Poll methods. =====


    /**
     * Send poll with DSL builder for options.
     *
     * Example:
     * ```
     * client.sendPoll(chatId, "Best language?") {
     *     answer("Kotlin")
     *     answer("Rust")
     *     answerHtml("<b>Java</b>")
     * }
     * ```
     */
    override suspend fun sendPoll(
        chatId: ChatId,
        question: String,
        options: List<InputPollOption>,
        businessConnectionId: String?,
        messageThreadId: Int?,
        questionParseMode: ParseMode?,
        questionEntities: List<MessageEntity>?,
        isAnonymous: Boolean?,
        type: PollType?,
        allowsMultipleAnswers: Boolean?,
        correctOptionId: Int?,
        explanation: String?,
        explanationParseMode: ParseMode?,
        explanationEntities: List<MessageEntity>?,
        openPeriod: Int?,
        closeDate: Instant?,
        isClosed: Boolean?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        allowPaidBroadcast: Boolean?,
        messageEffectId: String?,
        suggestedPostParameters: SuggestedPostParameters?,
        replyParameters: ReplyParameters?,
        replyMarkup: ReplyMarkup?
    ): Message {
        require(question.length in 1..300) { "The question must be between 1 and 300 characters" }
        require(options.size in 2..10) { "The survey should include between 2 and 10 answer options" }
        require(options.all { it.text.isNotEmpty() && it.text.length <= 100 }) {
            "Option text cannot be empty or longer than 100 characters"
        }
        require(options.map { it.text }.distinct().size == options.size) {
            "Answers should not be repeated"
        }

        return call("sendPoll") {
            parameter("chat_id", chatId.toApiParam())
            parameter("question", question)
            parameter("options", json.encodeToString(options))
            parameter("business_connection_id", businessConnectionId)
            parameter("message_thread_id", messageThreadId)
            parameter("question_parse_mode", questionParseMode)
            parameter("question_entities", questionEntities)
            parameter("is_anonymous", isAnonymous)
            parameter("type", type)
            parameter("allows_multiple_answers", allowsMultipleAnswers)
            parameter("correct_option_id", correctOptionId)
            parameter("explanation", explanation)
            parameter("explanation_parse_mode", explanationParseMode)
            parameter("explanation_entities", explanationEntities)
            parameter("open_period", openPeriod)
            parameter("close_date", closeDate)
            parameter("is_closed", isClosed)
            parameter("disable_notification", disableNotification)
            parameter("protect_content", protectContent)
            parameter("allow_paid_broadcast", allowPaidBroadcast)
            parameter("message_effect_id", messageEffectId)
            parameter("reply_parameters", replyParameters)
            parameter("suggested_post_parameters", suggestedPostParameters)
            parameter("reply_markup", replyMarkup)
        }
    }

    suspend fun sendPoll(
        chatId: ChatId,
        question: String,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        questionParseMode: ParseMode? = null,
        questionEntities: List<MessageEntity>? = null,
        isAnonymous: Boolean? = null,
        type: PollType? = null,
        allowsMultipleAnswers: Boolean? = null,
        correctOptionId: Int? = null,
        explanation: String? = null,
        explanationParseMode: ParseMode? = null,
        explanationEntities: List<MessageEntity>? = null,
        openPeriod: Int? = null,
        closeDate: Instant? = null,
        isClosed: Boolean? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        suggestedPostParameters: SuggestedPostParameters? = null,
        replyParameters: ReplyParameters? = null,
        replyMarkup: ReplyMarkup? = null,
        block: PollOptionsBuilder.() -> Unit
    ): Message = sendPoll(
        chatId, question, PollOptionsBuilder.build(block), businessConnectionId,
        messageThreadId, questionParseMode, questionEntities, isAnonymous, type,
        allowsMultipleAnswers, correctOptionId, explanation, explanationParseMode,
        explanationEntities, openPeriod, closeDate, isClosed, disableNotification,
        protectContent, allowPaidBroadcast, messageEffectId, suggestedPostParameters,
        replyParameters, replyMarkup
    )

    // ===== Check list methods. =====

    override suspend fun sendChecklist(
        chatId: ChatId,
        checklist: InputChecklist,
        businessConnectionId: String,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        messageEffectId: String?,
        replyParameters: ReplyParameters?,
        replyMarkup: ReplyMarkup?
    ): Message = call("sendChecklist") {
        parameter("chat_id", chatId.toApiParam())
        parameter("checklist", checklist)
        parameter("business_connection_id", businessConnectionId)
        parameter("disable_notification", disableNotification)
        parameter("protect_content", protectContent)
        parameter("message_effect_id", messageEffectId)
        parameter("reply_parameters", replyParameters)
        parameter("reply_markup", replyMarkup)
    }

    // ===== Dice methods. =====

    override suspend fun sendDice(
        chatId: ChatId,
        businessConnectionId: String?,
        messageThreadId: Int?,
        directMessagesTopicId: Int?,
        emoji: String?,
        disableNotification: Boolean?,
        protectContent: Boolean?,
        allowPaidBroadcast: Boolean?,
        messageEffectId: String?,
        suggestedPostParameters: SuggestedPostParameters?,
        replyParameters: ReplyParameters?,
        replyMarkup: ReplyMarkup?
    ): Message {
        if (emoji != null && emoji.isNotEmpty()) {
            require(emoji in listOf(
                "🎲",
                "🎯",
                "🏀",
                "⚽",
                "🎳",
                "🎰"
            )) { "This emoji is not allowed." }
        }

        return call("sendDice") {
            parameter("chat_id", chatId.toApiParam())
            parameter("business_connection_id", businessConnectionId)
            parameter("message_thread_id", messageThreadId)
            parameter("direct_messages_topic_id", directMessagesTopicId)
            parameter("emoji", emoji)
            parameter("disable_notification", disableNotification)
            parameter("protect_content", protectContent)
            parameter("allow_paid_broadcast", allowPaidBroadcast)
            parameter("message_effect_id", messageEffectId)
            parameter("suggested_post_parameters", suggestedPostParameters)
            parameter("reply_parameters", replyParameters)
            parameter("reply_markup", replyMarkup)
        }
    }

    override suspend fun sendChatAction(
        chatId: ChatId,
        action: ChatAction,
        businessConnectionId: String?,
        messageThreadId: Int?
    ): Boolean = call("sendChatAction") {
        parameter("chat_id", chatId.toApiParam())
        parameter("action", action)
        parameter("business_connection_id", businessConnectionId)
        parameter("message_thread_id", messageThreadId)
    }

    // ===== Message Reaction methods. =====

    override suspend fun setMessageReaction(
        chatId: ChatId,
        messageId: Int,
        reaction: List<ReactionType>,
        isBig: Boolean?
    ): Boolean = call("setMessageReaction") {
        parameter("chat_id", chatId.toApiParam())
        parameter("message_id", messageId)
        parameter("reaction", json.encodeToString(reaction))
        parameter("is_big", isBig)
    }

    suspend fun setMessageReaction(
        chatId: ChatId,
        messageId: Int,
        isBig: Boolean? = null,
        block: ReactionBuilder.() -> Unit = {},
    ): Boolean {
        val builder = ReactionBuilder().apply(block)
        return setMessageReaction(chatId, messageId, builder.reactions, isBig)
    }

    // ===== User files and emoji methods. =====

    override suspend fun getUserProfilePhotos(
        userId: Int,
        offset: Int?,
        limit: Int?
    ): UserProfilePhotos {
        if (limit != null) {
            require(limit in 1..100) { "The limit can range from 1 to 100." }
        }

        return call("getUserProfilePhotos") {
            parameter("user_id", userId)
            parameter("offset", offset)
            parameter("limit", limit)
        }
    }

    override suspend fun getUserProfileAudios(
        userId: Int,
        offset: Int?,
        limit: Int?
    ): UserProfileAudios {
        if (limit != null) {
            require(limit in 1..100) { "The limit can range from 1 to 100." }
        }

        return call("getUserProfileAudios") {
            parameter("user_id", userId)
            parameter("offset", offset)
            parameter("limit", limit)
        }
    }

    override suspend fun setUserEmojiStatus(
        userId: Int,
        emojiStatusCustomEmojiId: String?,
        emojiStatusExpirationDate: String?
    ): Boolean = call("setUserEmojiStatus") {
        parameter("user_id", userId)
        parameter("emoji_status_custom_emoji_id", emojiStatusCustomEmojiId)
        parameter("emoji_status_expiration_date", emojiStatusExpirationDate)
    }

    // ===== Get file method. =====

    override suspend fun getFile(fileId: String): File =
        call("getFile") { parameter("file_id", fileId) }

    override suspend fun banChatMember(
        chatId: ChatId,
        userId: Long,
        untilDate: Instant?,
        revokeMessages: Boolean?
    ): Boolean = call("banChatMember") {
        parameter("chat_id", chatId.toApiParam())
        parameter("user_id", userId)
        parameter("until_date", untilDate)
        parameter("revoke_messages", revokeMessages)
    }

    override suspend fun unbanChatMember(
        chatId: ChatId,
        userId: Long,
        onlyIfBanned: Boolean?
    ): Boolean = call("unbanChatMember") {
        parameter("chat_id", chatId.toApiParam())
        parameter("user_id", userId)
        parameter("only_if_banned", onlyIfBanned)
    }

    override suspend fun restrictChatMember(
        chatId: ChatId,
        userId: Long,
        permissions: ChatPermissions,
        untilDate: Instant?,
        useIndependentChatPermissions: Boolean?,
    ): Boolean = call("restrictChatMember") {
        parameter("chat_id", chatId.toApiParam())
        parameter("user_id", userId)
        parameter("permissions", json.encodeToString(permissions))
        parameter("until_date", untilDate)
    }

    suspend fun restrictChatMember(
        chatId: ChatId,
        userId: Long,
        untilDate: Instant? = null,
        useIndependentChatPermissions: Boolean? = null,
        block: ChatPermissionsBuilder.() -> Unit = {}
    ): Boolean {
        val builder = ChatPermissionsBuilder().apply(block)
        return restrictChatMember(chatId, userId, builder.build(), untilDate, useIndependentChatPermissions)
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