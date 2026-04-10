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
import io.telegramkt.model.bot.command.BotCommand
import io.telegramkt.model.bot.command.BotCommandScope
import io.telegramkt.model.bot.command.BotCommandsBuilder
import io.telegramkt.model.bot.command.botCommands
import io.telegramkt.model.bot.description.BotDescription
import io.telegramkt.model.bot.description.BotShortDescription
import io.telegramkt.model.bot.name.BotName
import io.telegramkt.model.business.BusinessConnection
import io.telegramkt.model.chat.ChatFullInfo
import io.telegramkt.model.chat.ChatId
import io.telegramkt.model.chat.action.ChatAction
import io.telegramkt.model.chat.administrator.ChatAdministratorRights
import io.telegramkt.model.chat.administrator.ChatAdministratorRightsBuilder
import io.telegramkt.model.chat.administrator.ChatPermissions
import io.telegramkt.model.chat.administrator.ChatPermissionsBuilder
import io.telegramkt.model.chat.invite.ChatInviteLink
import io.telegramkt.model.chat.member.ChatMember
import io.telegramkt.model.checklist.input.InputChecklist
import io.telegramkt.model.contact.Contact
import io.telegramkt.model.contact.ContactRequestBuilder
import io.telegramkt.model.file.File
import io.telegramkt.model.file.input.InputFile
import io.telegramkt.model.forum.ForumTopic
import io.telegramkt.model.forum.topic.TopicIconColor
import io.telegramkt.model.gift.AcceptedGiftTypes
import io.telegramkt.model.gift.Gifts
import io.telegramkt.model.gift.owned.OwnedGifts
import io.telegramkt.model.keyboard.reply.InlineKeyboardMarkup
import io.telegramkt.model.keyboard.reply.ReplyMarkup
import io.telegramkt.model.keyboard.reply.parameters.ReplyParameters
import io.telegramkt.model.location.Location
import io.telegramkt.model.location.Venue
import io.telegramkt.model.location.builder.LocationRequestBuilder
import io.telegramkt.model.location.builder.VenueRequestBuilder
import io.telegramkt.model.media.input.MediaGroupBuilder
import io.telegramkt.model.media.input.AlbumableMedia
import io.telegramkt.model.media.input.InputProfilePhoto
import io.telegramkt.model.media.input.InputProfilePhotoBuilder
import io.telegramkt.model.menu.button.MenuButton
import io.telegramkt.model.message.Message
import io.telegramkt.model.message.MessageId
import io.telegramkt.model.message.entity.MessageEntity
import io.telegramkt.model.poll.PollType
import io.telegramkt.model.poll.input.InputPollOption
import io.telegramkt.model.poll.input.PollOptionsBuilder
import io.telegramkt.model.premium.PremiumSubscriptionPeriod
import io.telegramkt.model.reaction.ReactionBuilder
import io.telegramkt.model.reaction.ReactionType
import io.telegramkt.model.star.StarAmount
import io.telegramkt.model.sticker.Sticker
import io.telegramkt.model.story.InputStoryContent
import io.telegramkt.model.story.InputStoryContentBuilder
import io.telegramkt.model.story.Story
import io.telegramkt.model.story.StoryActivePeriod
import io.telegramkt.model.story.area.StoryArea
import io.telegramkt.model.suggested.SuggestedPostParameters
import io.telegramkt.model.update.Update
import io.telegramkt.model.user.User
import io.telegramkt.model.user.boost.UserChatBoosts
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
        // Uncomment for logging.
//        install(Logging) {
//            level = LogLevel.ALL
//        }
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
        chatId: Long,
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
    ): Message = call("sendDocument") {
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
        businessConnectionId: String?,
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
        return sendMediaGroup(
            chatId, builder.media, businessConnectionId, messageThreadId, directMessagesTopicId,
            disableNotification, protectContent, allowPaidBroadcast, messageEffectId, replyParameters
        )
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
        return sendMediaGroup(
            chatId, builder.media, businessConnectionId, messageThreadId, directMessagesTopicId,
            disableNotification, protectContent, allowPaidBroadcast, messageEffectId, replyParameters
        )
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
        return sendMediaGroup(
            chatId, builder.media, businessConnectionId, messageThreadId, directMessagesTopicId,
            disableNotification, protectContent, allowPaidBroadcast, messageEffectId, replyParameters
        )
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
        return sendLocation(
            chatId, builder.build(), businessConnectionId, messageThreadId, directMessagesTopicId,
            disableNotification, protectContent, allowPaidBroadcast, messageEffectId, suggestedPostParameters,
            replyParameters, replyMarkup
        )
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
        return sendVenue(
            chatId,
            builder.build(),
            businessConnectionId,
            messageThreadId,
            directMessagesTopicId,
            disableNotification,
            protectContent,
            allowPaidBroadcast,
            messageEffectId,
            suggestedPostParameters,
            replyParameters,
            replyMarkup
        )
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
    ): Message = sendContact(
        chatId, contact.phoneNumber, contact.firstName, contact.lastName, contact.vCard,
        businessConnectionId, messageThreadId, directMessagesTopicId, disableNotification, protectContent,
        allowPaidBroadcast, messageEffectId, suggestedPostParameters, replyParameters, replyMarkup
    )

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
        return sendContact(
            chatId, builder.build(), businessConnectionId, messageThreadId, directMessagesTopicId,
            disableNotification, protectContent, allowPaidBroadcast, messageEffectId, suggestedPostParameters,
            replyParameters, replyMarkup
        )
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
            require(
                emoji in listOf(
                    "🎲",
                    "🎯",
                    "🏀",
                    "⚽",
                    "🎳",
                    "🎰"
                )
            ) { "This emoji is not allowed." }
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
        userId: Long,
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
        userId: Long,
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
        userId: Long,
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

    // ===== Ban/unban user methods. =====

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

    // ===== Restrict/Promote chat member methods. =====

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

    override suspend fun promoteChatMember(
        chatId: ChatId,
        userId: Long,
        isAnonymous: Boolean?,
        canManageChat: Boolean?,
        canDeleteMessages: Boolean?,
        canManageVideoChats: Boolean?,
        canRestrictMembers: Boolean?,
        canPromoteMembers: Boolean?,
        canChangeInfo: Boolean?,
        canInviteUsers: Boolean?,
        canPostStories: Boolean?,
        canEditStories: Boolean?,
        canDeleteStories: Boolean?,
        canPostMessages: Boolean?,
        canEditMessages: Boolean?,
        canPinMessages: Boolean?,
        canManageTopics: Boolean?,
        canManageDirectMessages: Boolean?,
        canManageTags: Boolean?
    ): Boolean = call("promoteChatMember") {
        parameter("chat_id", chatId.toApiParam())
        parameter("user_id", userId)
        parameter("is_anonymous", isAnonymous)
        parameter("can_manage_chat", canManageChat)
        parameter("can_delete_messages", canDeleteMessages)
        parameter("can_manage_video_chats", canManageVideoChats)
        parameter("can_restrict_members", canRestrictMembers)
        parameter("can_promote_members", canPromoteMembers)
        parameter("can_change_info", canChangeInfo)
        parameter("can_invite_users", canInviteUsers)
        parameter("can_post_stories", canPostStories)
        parameter("can_edit_stories", canEditStories)
        parameter("can_delete_stories", canDeleteStories)
        parameter("can_post_messages", canPostMessages)
        parameter("can_edit_messages", canEditMessages)
        parameter("can_pin_messages", canPinMessages)
        parameter("can_manage_topics", canManageTopics)
        parameter("can_manage_direct_messages", canManageDirectMessages)
        parameter("can_manage_tags", canManageTags)
    }

    suspend fun promoteChatMember(
        chatId: ChatId,
        userId: Long,
        chatAdministratorRights: ChatAdministratorRights
    ): Boolean = promoteChatMember(
        chatId = chatId,
        userId = userId,
        isAnonymous = chatAdministratorRights.isAnonymous,
        canManageChat = chatAdministratorRights.canManageChat,
        canDeleteMessages = chatAdministratorRights.canDeleteMessages,
        canManageVideoChats = chatAdministratorRights.canManageVideoChats,
        canRestrictMembers = chatAdministratorRights.canRestrictMembers,
        canPromoteMembers = chatAdministratorRights.canPromoteMembers,
        canChangeInfo = chatAdministratorRights.canChangeInfo,
        canInviteUsers = chatAdministratorRights.canInviteUsers,
        canPostStories = chatAdministratorRights.canPostStories,
        canEditStories = chatAdministratorRights.canEditStories,
        canDeleteStories = chatAdministratorRights.canDeleteStories,
        canPostMessages = chatAdministratorRights.canPostMessages,
        canEditMessages = chatAdministratorRights.canEditMessages,
        canPinMessages = chatAdministratorRights.canPinMessages,
        canManageTopics = chatAdministratorRights.canManageTopics,
        canManageDirectMessages = chatAdministratorRights.canManageDirectMessages,
        canManageTags = chatAdministratorRights.canManageTags
    )

    suspend fun promoteChatMember(
        chatId: ChatId,
        userId: Long,
        block: ChatAdministratorRightsBuilder.() -> Unit = {}
    ): Boolean {
        val builder = ChatAdministratorRightsBuilder().apply(block)
        return promoteChatMember(chatId, userId, builder.build())
    }

    // ===== Set chat administrator custom title method. =====

    override suspend fun setChatAdministratorCustomTitle(
        chatId: ChatId,
        userId: Long,
        customTitle: String
    ): Boolean {
        require(customTitle.length in 0..16) { "Chat title must be between 0 and 16 characters." }

        return call("setChatAdministratorCustomTitle") {
            parameter("chat_id", chatId.toApiParam())
            parameter("user_id", userId)
            parameter("custom_title", customTitle)
        }
    }

    // ===== Set chat member tag method. =====
    override suspend fun setChatMemberTag(
        chatId: ChatId,
        userId: Long,
        tag: String
    ): Boolean {
        require(tag.length in 0..16) { "Tag must be between 0 and 16 characters." }

        return call("setChatMemberTag") {
            parameter("chat_id", chatId.toApiParam())
            parameter("user_id", userId)
            parameter("tag", tag)
        }
    }

    // ===== Ban/unban chat sender chat methods. =====

    override suspend fun banChatSenderChat(
        chatId: ChatId,
        senderChatId: Long
    ): Boolean = call("banChatSenderChat") {
        parameter("chat_id", chatId.toApiParam())
        parameter("sender_chat_id", senderChatId)
    }

    override suspend fun unbanChatSenderChat(
        chatId: ChatId,
        senderChatId: Long
    ): Boolean = call("unbanChatSenderChat") {
        parameter("chat_id", chatId.toApiParam())
        parameter("sender_chat_id", senderChatId)
    }

    // ===== Set chat permissions methods. =====

    override suspend fun setChatPermissions(
        chatId: ChatId,
        permissions: ChatPermissions,
        useIndependentChatPermissions: Boolean?
    ): Boolean = call("setChatPermissions") {
        parameter("chat_id", chatId.toApiParam())
        parameter("permissions", permissions)
        parameter("use_independent_chat_permissions", useIndependentChatPermissions)
    }

    suspend fun setChatPermissions(
        chatId: ChatId,
        useIndependentChatPermissions: Boolean? = null,
        block: ChatPermissionsBuilder.() -> Unit = {},
    ): Boolean {
        val builder = ChatPermissionsBuilder().apply(block)
        return setChatPermissions(chatId, builder.build(), useIndependentChatPermissions)
    }

    override suspend fun exportChatInviteLink(chatId: ChatId): String? = call("exportChatInviteLink") {
        parameter("chat_id", chatId.toApiParam())
    }

    // ===== Create/edit/revoke chat invite link methods. =====

    override suspend fun createChatInviteLink(
        chatId: ChatId,
        name: String?,
        expireDate: Instant?,
        memberLimit: Int?,
        createsJoinRequest: Boolean?
    ): ChatInviteLink {
        if (name != null) {
            require(name.length in 0..32) { "Name must be between 0 and 32 characters." }
        }

        return call("createChatInviteLink") {
            parameter("chat_id", chatId.toApiParam())
            parameter("name", name)
            parameter("expire_date", expireDate)
            parameter("member_limit", memberLimit)
            parameter("creates_join_request", createsJoinRequest)
        }
    }

    override suspend fun editChatInviteLink(
        chatId: ChatId,
        inviteLink: String,
        name: String?,
        expireDate: Instant?,
        memberLimit: Int?,
        createsJoinRequest: Boolean?
    ): ChatInviteLink {
        if (name != null) {
            require(name.length in 0..32) { "Name must be between 0 and 32 characters." }
        }

        return call("createChatInviteLink") {
            parameter("chat_id", chatId.toApiParam())
            parameter("invite_link", inviteLink)
            parameter("name", name)
            parameter("expire_date", expireDate)
            parameter("member_limit", memberLimit)
            parameter("creates_join_request", createsJoinRequest)
        }
    }

    override suspend fun revokeChatInviteLink(
        chatId: ChatId,
        inviteLink: String
    ): ChatInviteLink = call("createChatInviteLink") {
        parameter("chat_id", chatId.toApiParam())
        parameter("invite_link", inviteLink)
    }

    // ===== Create/edit/ chat subscription invite link methods. =====

    override suspend fun createChatSubscriptionInviteLink(
        chatId: ChatId,
        subscriptionPeriod: Duration,
        subscriptionPrice: Int,
        name: String?
    ): ChatInviteLink {
        require(subscriptionPeriod.inWholeSeconds in 0..2592000) {
            "Subscription period can range from 1 second to 30 days."
        }
        require(subscriptionPrice in 1..10000) {
            "Subscription price can range from 1 to 10,000 stars."
        }

        if (name != null) {
            require(name.length in 0..32) { "Name must be between 0 and 32 characters." }
        }

        return call("createChatInviteLink") {
            parameter("chat_id", chatId.toApiParam())
            parameter("name", name)
            parameter("subscription_period", subscriptionPeriod)
            parameter("subscription_price", subscriptionPrice)
        }
    }

    override suspend fun editChatSubscriptionInviteLink(
        chatId: ChatId,
        inviteLink: String,
        name: String?
    ): ChatInviteLink {
        if (name != null) {
            require(name.length in 0..32) { "Name must be between 0 and 32 characters." }
        }

        return call("createChatInviteLink") {
            parameter("chat_id", chatId.toApiParam())
            parameter("invite_link", inviteLink)
            parameter("name", name)
        }
    }

    // ===== Approve/decline chat join request methods. =====

    override suspend fun approveChatJoinRequest(
        chatId: ChatId,
        userId: Long
    ): Boolean = call("approveChatJoinRequest") {
        parameter("chat_id", chatId.toApiParam())
        parameter("user_id", userId)
    }

    override suspend fun declineChatJoinRequest(
        chatId: ChatId,
        userId: Long
    ): Boolean = call("declineChatJoinRequest") {
        parameter("chat_id", chatId.toApiParam())
        parameter("user_id", userId)
    }

    // ===== Set/delete chat photo methods. =====

    override suspend fun setChatPhoto(
        chatId: ChatId,
        photo: InputFile
    ): Boolean = call("setChatPhoto") {
        parameter("chat_id", chatId.toApiParam())
        parameter("photo", photo)
    }

    override suspend fun deleteChatPhoto(chatId: ChatId): Boolean = call("deleteChatPhoto") {
        parameter("chat_id", chatId.toApiParam())
    }

    // ===== Set chat title/description methods. =====

    override suspend fun setChatTitle(chatId: ChatId, title: String): Boolean {
        require(title.length in 1..128) { "Chat title must be between 0 and 128 characters." }

        return call("setChatTitle") {
            parameter("chat_id", chatId.toApiParam())
            parameter("title", title)
        }
    }

    override suspend fun setChatDescription(
        chatId: ChatId,
        description: String
    ): Boolean {
        require(description.length in 1..128) { "Chat description must be between 0 and 128 characters." }

        return call("setChatDescription") {
            parameter("chat_id", chatId.toApiParam())
            parameter("description", description)
        }
    }

    // ===== Pin/unpin messages methods. =====

    override suspend fun pinChatMessage(
        chatId: ChatId,
        messageId: Int,
        businessConnectionId: String?,
        disableNotification: Boolean?
    ): Boolean = call("pinChatMessage") {
        parameter("chat_id", chatId.toApiParam())
        parameter("message_id", messageId)
        parameter("business_connection_id", businessConnectionId)
        parameter("disable_notification", disableNotification)
    }

    override suspend fun unpinChatMessage(
        chatId: ChatId,
        messageId: Int,
        businessConnectionId: String?
    ): Boolean = call("unpinChatMessage") {
        parameter("chat_id", chatId.toApiParam())
        parameter("message_id", messageId)
        parameter("business_connection_id", businessConnectionId)
    }

    override suspend fun unpinAllChatMessages(chatId: ChatId): Boolean = call("unpinAllChatMessages") {
        parameter("chat_id", chatId.toApiParam())
    }

    // ===== Leave chat method. =====

    override suspend fun leaveChat(chatId: ChatId): Boolean = call("leaveChat") {
        parameter("chat_id", chatId.toApiParam())
    }

    // ===== Get chat method. =====
    override suspend fun getChat(chatId: ChatId): ChatFullInfo = call("getChat") {
        parameter("chat_id", chatId.toApiParam())
    }

    // ===== Get chat administrators method. =====
    override suspend fun getChatAdministrators(chatId: ChatId): List<ChatMember> = call("getChatAdministrators") {
        parameter("chat_id", chatId.toApiParam())
    }

    // ===== Get chat member and chat member count methods. =====
    override suspend fun getChatMemberCount(chatId: ChatId): Int = call("getChatMemberCount") {
        parameter("chat_id", chatId.toApiParam())
    }

    override suspend fun getChatMember(
        chatId: ChatId,
        userId: Long
    ): ChatMember = call("getChatMember") {
        parameter("chat_id", chatId.toApiParam())
        parameter("user_id", userId)
    }

    // ===== Set and delete chat sticker set methods. =====
    override suspend fun setChatStickerSet(
        chatId: ChatId,
        stickerSetName: String
    ): Boolean = call("setChatStickerSet") {
        parameter("chat_id", chatId.toApiParam())
        parameter("sticker_set_name", stickerSetName)
    }

    override suspend fun deleteChatStickerSet(chatId: ChatId): Boolean = call("deleteChatStickerSet") {
        parameter("chat_id", chatId.toApiParam())
    }

    // ===== Get forum topic icon stickers method. =====
    override suspend fun getForumTopicIconStickers(): List<Sticker> = call("getForumTopicIconStickers")

    // ===== Create/edit/close/reopen/delete forum topic methods. =====

    override suspend fun createForumTopic(
        chatId: ChatId,
        name: String,
        iconColor: TopicIconColor?,
        iconCustomEmojiId: String?
    ): ForumTopic {
        require(name.length in 0..128) { "Topic name must be between 0 and 128 characters." }

        return call("createForumTopic") {
            parameter("chat_id", chatId.toApiParam())
            parameter("name", name)
            parameter("icon_color", iconColor)
            parameter("icon_custom_emoji_id", iconCustomEmojiId)
        }
    }

    override suspend fun editForumTopic(
        chatId: ChatId,
        messageThreadId: Int,
        name: String?,
        iconCustomEmojiId: String?
    ): Boolean {
        if (name != null) {
            require(name.length in 0..128) { "Topic name must be between 0 and 128 characters." }
        }

        return call("editForumTopic") {
            parameter("chat_id", chatId.toApiParam())
            parameter("message_thread_id", messageThreadId)
            parameter("name", name)
            parameter("icon_custom_emoji_id", iconCustomEmojiId)
        }
    }

    override suspend fun closeForumTopic(
        chatId: ChatId,
        messageThreadId: Int
    ): Boolean = call("closeForumTopic") {
        parameter("chat_id", chatId.toApiParam())
        parameter("message_thread_id", messageThreadId)
    }

    override suspend fun reopenForumTopic(
        chatId: ChatId,
        messageThreadId: Int
    ): Boolean = call("reopenForumTopic") {
        parameter("chat_id", chatId.toApiParam())
        parameter("message_thread_id", messageThreadId)
    }

    override suspend fun deleteForumTopic(
        chatId: ChatId,
        messageThreadId: Int
    ): Boolean = call("deleteForumTopic") {
        parameter("chat_id", chatId.toApiParam())
        parameter("message_thread_id", messageThreadId)
    }


    // ===== Unpin all forum topic messages method. =====
    override suspend fun unpinAllForumTopicMessages(
        chatId: ChatId,
        messageThreadId: Int
    ): Boolean = call("unpinAllForumTopicMessages") {
        parameter("chat_id", chatId.toApiParam())
        parameter("message_thread_id", messageThreadId)
    }

    // ===== Edit/close/hide/unhide general forum topic methods. =====

    override suspend fun editGeneralForumTopic(
        chatId: ChatId,
        name: String
    ): Boolean {
        require(name.length in 0..128) { "Topic name must be between 0 and 128 characters." }

        return call("editGeneralForumTopic") {
            parameter("chat_id", chatId.toApiParam())
            parameter("name", name)
        }
    }

    override suspend fun closeGeneralForumTopic(chatId: ChatId): Boolean = call("closeGeneralForumTopic") {
        parameter("chat_id", chatId.toApiParam())
    }

    override suspend fun reopenGeneralForumTopic(chatId: ChatId): Boolean = call("reopenGeneralForumTopic") {
        parameter("chat_id", chatId.toApiParam())
    }

    override suspend fun hideGeneralForumTopic(chatId: ChatId): Boolean = call("hideGeneralForumTopic") {
        parameter("chat_id", chatId.toApiParam())
    }

    override suspend fun unhideGeneralForumTopic(chatId: ChatId): Boolean = call("unhideGeneralForumTopic") {
        parameter("chat_id", chatId.toApiParam())
    }

    // ===== Unpin all general forum topic messages method. =====
    override suspend fun unpinAllGeneralForumTopicMessages(chatId: ChatId): Boolean =
        call("unpinAllGeneralForumTopicMessages") {
            parameter("chat_id", chatId.toApiParam())
        }

    // ===== Get user chat boosts method. =====
    override suspend fun getUserChatBoosts(
        chatId: ChatId,
        userId: Long
    ): UserChatBoosts = call("getUserChatBoosts") {
        parameter("chat_id", chatId.toApiParam())
        parameter("user_id", userId)
    }

    // ===== Get business connection method. =====
    override suspend fun getBusinessConnection(businessConnectionId: String): BusinessConnection =
        call("getBusinessConnection") {
            parameter("business_connection_id", businessConnectionId)
        }

    // ===== Get/replace managed bot token methods. =====
    override suspend fun getManagedBotToken(userId: Long): String = call("getManagedBotToken") {
        parameter("user_id", userId)
    }

    override suspend fun replaceManagedBotToken(userId: Long): String = call("replaceManagedBotToken") {
        parameter("user_id", userId)
    }

    // ===== Set/get/delete my commands methods. =====
    override suspend fun setMyCommands(
        commands: List<BotCommand>,
        scope: BotCommandScope?,
        languageCode: String?
    ): Boolean = call("setMyCommands") {
        parameter("commands", json.encodeToString(commands))
        parameter("scope", scope?.let { json.encodeToString(it) })
        parameter("language_code", languageCode)
    }

    suspend fun setMyCommands(commands: List<BotCommandsBuilder.ScopedCommands>) {
        commands.forEach { scoped ->
            setMyCommands(scoped.commands, scoped.scope, scoped.languageCode)
        }
    }

    suspend fun setMyCommands(block: BotCommandsBuilder.() -> Unit) {
        val configs = botCommands(block)
        configs.forEach { config ->
            setMyCommands(config.commands, config.scope, config.languageCode)
        }
    }

    override suspend fun deleteMyCommands(
        scope: BotCommandScope?,
        languageCode: String?
    ): Boolean = call("deleteMyCommands") {
        parameter("scope", scope?.let { json.encodeToString(it) })
        parameter("language_code", languageCode)
    }

    override suspend fun getMyCommands(
        scope: BotCommandScope?,
        languageCode: String?
    ): List<BotCommand> = call("getMyCommands") {
        parameter("scope", scope?.let { json.encodeToString(it) })
        parameter("language_code", languageCode)
    }

    // ===== Set/get my name methods. =====
    override suspend fun setMyName(name: String, languageCode: String?): Boolean {
        if (languageCode.isNullOrEmpty()) require(name.length in 0..64) { "Name must be between 0 and 64 characters." }
        else require(name.length in 1..64) {
            "Name with language code must be between 0 and 64 characters(0 for delete name for this language code)."
        }

        return call("setMyName") {
            parameter("name", name)
            parameter("language_code", languageCode)
        }
    }

    override suspend fun getMyName(languageCode: String?): BotName = call("getMyName") {
        parameter("language_code", languageCode)
    }

    // ===== Set/get my description methods. =====
    override suspend fun setMyDescription(description: String, languageCode: String?): Boolean {
        if (languageCode.isNullOrEmpty()) require(description.length in 0..512) {
            "Description must be between 0 and 512 characters."
        }
        else require(description.length in 1..512) {
            "Description with language code must be between 0 and 512 characters(0 for delete name for this language code)."
        }

        return call("setMyDescription") {
            parameter("description", description)
            parameter("language_code", languageCode)
        }
    }

    override suspend fun getMyDescription(languageCode: String?): BotDescription = call("getMyDescription") {
        parameter("language_code", languageCode)
    }

    // ===== Set/get my short description methods. =====

    override suspend fun setMyShortDescription(
        shortDescription: String,
        languageCode: String?
    ): Boolean {
        if (languageCode.isNullOrEmpty()) require(shortDescription.length in 0..120) {
            "Short description must be between 0 and 120 characters."
        }
        else require(shortDescription.length in 1..120) {
            "Short description with language code must be between 0 and 120 characters(0 for delete name for this language code)."
        }

        return call("setMyShortDescription") {
            parameter("short_description", shortDescription)
            parameter("language_code", languageCode)
        }
    }

    override suspend fun getMyShortDescription(languageCode: String?): BotShortDescription =
        call("getMyShortDescription") {
            parameter("language_code", languageCode)
        }

    // ===== Set/remove my profile photo methods. =====

    override suspend fun setMyProfilePhoto(photo: InputProfilePhoto): Boolean = call("setMyProfilePhoto") {
        parameter("photo", photo)
    }

    suspend fun setMyProfilePhoto(block: InputProfilePhotoBuilder.() -> Unit): Boolean {
        val builder = InputProfilePhotoBuilder().apply(block)
        return setMyProfilePhoto(builder.build())
    }

    override suspend fun removeMyProfilePhoto(): Boolean = call("removeMyProfilePhoto")

    // ===== Set/get chat menu button methods.. =====
    override suspend fun setChatMenuButton(
        chatId: Long?,
        menuButton: MenuButton?
    ): Boolean = call("setChatMenuButton") {
        parameter("chat_id", chatId)
        parameter("menu_button", menuButton)
    }

    override suspend fun getChatMenuButton(chatId: Long?): MenuButton = call("getChatMenuButton") {
        parameter("chat_id", chatId)
    }

    // ===== Set/get my default administrator rights methods.. =====
    override suspend fun setMyDefaultAdministratorRights(
        rights: ChatAdministratorRights?,
        forChannels: Boolean?
    ): Boolean = call("setMyDefaultAdministratorRights") {
        parameter("rights", rights)
        parameter("for_channels", forChannels)
    }

    suspend fun setMyDefaultAdministratorRights(
        forChannels: Boolean? = null,
        block: ChatAdministratorRightsBuilder.() -> Unit = {}
    ): Boolean {
        val builder = ChatAdministratorRightsBuilder().apply(block)
        return setMyDefaultAdministratorRights(builder.build(), forChannels)
    }

    override suspend fun getMyDefaultAdministratorRights(forChannels: Boolean?): ChatAdministratorRights =
        call("getMyDefaultAdministratorRights") {
            parameter("for_channels", forChannels)
        }

    // ===== Get available gifts and send gift methods. =====

    override suspend fun getAvailableGifts(): Gifts = call("getAvailableGifts")

    override suspend fun sendGift(
        giftId: String,
        userId: Long?,
        chatId: ChatId?,
        payForUpgrade: Boolean?,
        text: String?,
        textParseMode: ParseMode?,
        textEntities: List<MessageEntity>?
    ): Boolean {
        if (!text.isNullOrEmpty()) require(text.length in 0..128) {
            "Text must be between 0 and 128 characters."
        }

        return call("sendGift") {
            parameter("chat_id", chatId)
            parameter("user_id", userId)
            parameter("gift_id", giftId)
            parameter("pay_for_upgrade", payForUpgrade)
            parameter("text", text)
            parameter("text_parse_mode", textParseMode)
            parameter("text_entities", textEntities)
        }
    }

    override suspend fun giftPremiumSubscription(
        userId: Int,
        period: PremiumSubscriptionPeriod,
        text: String?,
        textParseMode: ParseMode?,
        textEntities: List<MessageEntity>?
    ): Boolean {
        if (!text.isNullOrEmpty()) require(text.length in 0..128) {
            "Text must be between 0 and 128 characters."
        }

        return call("giftPremiumSubscription") {
            parameter("user_id", userId)
            parameter("month_count", period.monthCount)
            parameter("star_count", period.starCount)
            parameter("text", text)
            parameter("text_parse_mode", textParseMode)
            parameter("text_entities", textEntities)
        }
    }

    // ===== Verify/remove verification user/chat methods. =====

    override suspend fun verifyUser(userId: Long, customDescription: String?): Boolean {
        if (!customDescription.isNullOrEmpty()) require(customDescription.length in 0..70) {
            "Custom description must be between 0 and 70 characters."
        }

        return call("verifyUser") {
            parameter("user_id", userId)
            parameter("custom_description", customDescription)
        }
    }

    override suspend fun verifyChat(
        chatId: ChatId,
        customDescription: String?
    ): Boolean {
        if (!customDescription.isNullOrEmpty()) require(customDescription.length in 0..70) {
            "Custom description must be between 0 and 70 characters."
        }

        return call("verifyChat") {
            parameter("chat_id", chatId.toApiParam())
            parameter("custom_description", customDescription)
        }
    }

    override suspend fun removeUserVerification(userId: Long): Boolean = call("removeUserVerification") {
        parameter("user_id", userId)
    }

    override suspend fun removeChatVerification(chatId: ChatId): Boolean = call("removeChatVerification") {
        parameter("chat_id", chatId.toApiParam())
    }

    // ===== Read/delete business messages methods. =====

    override suspend fun readBusinessMessage(
        businessConnectionId: String,
        chatId: Long,
        messageId: Long
    ): Boolean = call("readBusinessMessage") {
        parameter("business_connection_id", businessConnectionId)
        parameter("chat_id", chatId)
        parameter("message_id", messageId)
    }

    override suspend fun deleteBusinessMessages(
        businessConnectionId: String,
        messageIds: List<Long>
    ): Boolean {
        require(messageIds.size in 1..100) {
            "messageIds must be between 1 and 100 items, got ${messageIds.size}."
        }

        return call("deleteBusinessMessages") {
            parameter("business_connection_id", businessConnectionId)
            parameter("message_ids", messageIds)
        }
    }

    // ===== Set business account name/username/bio/photo/gift settings methods. =====

    override suspend fun setBusinessAccountName(
        businessConnectionId: String,
        fistName: String,
        lastName: String?
    ): Boolean {
        require(fistName.length in 1..64) {
            "First name must be between 0 and 64 characters."
        }

        if (!lastName.isNullOrEmpty()) require(lastName.length in 0..64) {
            "Last name must be between 1 and 64 characters."
        }

        return call("setBusinessAccountName") {
            parameter("business_connection_id", businessConnectionId)
            parameter("first_name", fistName)
            parameter("last_name", lastName)
        }
    }

    override suspend fun setBusinessAccountUsername(
        businessConnectionId: String,
        username: String?
    ): Boolean {
        if (!username.isNullOrEmpty()) require(username.length in 0..32) {
            "Username must be between 0 and 32 characters."
        }

        return call("setBusinessAccountUsername") {
            parameter("business_connection_id", businessConnectionId)
            parameter("username", username)
        }
    }

    override suspend fun setBusinessAccountBio(
        businessConnectionId: String,
        bio: String?
    ): Boolean {
        if (!bio.isNullOrEmpty()) require(bio.length in 0..140) {
            "Bio must be between 0 and 140 characters."
        }

        return call("setBusinessAccountBio") {
            parameter("business_connection_id", businessConnectionId)
            parameter("bio", bio)
        }
    }

    override suspend fun setBusinessAccountProfilePhoto(
        businessConnectionId: String,
        photo: InputProfilePhoto,
        isPublic: Boolean?
    ): Boolean = call("setBusinessAccountProfilePhoto") {
        parameter("business_connection_id", businessConnectionId)
        parameter("photo", photo)
        parameter("is_public", isPublic)
    }

    suspend fun setBusinessAccountProfilePhoto(
        businessConnectionId: String,
        isPublic: Boolean? = null,
        block: InputProfilePhotoBuilder.() -> Unit = {}
    ): Boolean {
        val builder = InputProfilePhotoBuilder().apply(block)
        return setBusinessAccountProfilePhoto(
            businessConnectionId = businessConnectionId,
            photo = builder.build(),
            isPublic = isPublic,
        )
    }

    override suspend fun removeBusinessAccountProfilePhoto(
        businessConnectionId: String,
        isPublic: Boolean?
    ): Boolean = call("removeBusinessAccountProfilePhoto") {
        parameter("business_connection_id", businessConnectionId)
        parameter("is_public", isPublic)
    }

    override suspend fun setBusinessAccountGiftSettings(
        businessConnectionId: String,
        showGiftButton: Boolean,
        acceptedGiftTypes: AcceptedGiftTypes
    ): Boolean = call("setBusinessAccountGiftSettings") {
        parameter("business_connection_id", businessConnectionId)
        parameter("show_gift_button", showGiftButton)
        parameter("accepted_gift_types", acceptedGiftTypes)
    }

    // ===== Get/transfer business account stars methods. =====

    override suspend fun getBusinessAccountStarBalance(businessConnectionId: String): StarAmount
        = call("getBusinessAccountStarBalance") {
            parameter("business_connection_id", businessConnectionId)
    }

    override suspend fun transferBusinessAccountStars(
        businessConnectionId: String,
        starAmount: Int
    ): Boolean = call("transferBusinessAccountStars") {
        parameter("business_connection_id", businessConnectionId)
        parameter("star_count", starAmount)
    }

    // ===== Get business account/user/chat gifts methods. =====

    override suspend fun getBusinessAccountGifts(
        businessConnectionId: String,
        excludeUnsaved: Boolean?,
        excludeSaved: Boolean?,
        excludeUnlimited: Boolean?,
        excludeLimitedUpgradable: Boolean?,
        excludeLimitedNonUpgradable: Boolean?,
        excludeUnique: Boolean?,
        excludeFromBlockchain: Boolean?,
        sortByPrice: Boolean?,
        offset: String?,
        limit: Int?
    ): OwnedGifts {
        if (limit != null) require(limit in 1..100) {
            "Limit must be between 1 and 100."
        }

        return call("getBusinessAccountGifts") {
            parameter("business_connection_id", businessConnectionId)
            parameter("exclude_unsaved", excludeUnsaved)
            parameter("exclude_saved", excludeSaved)
            parameter("exclude_unlimited", excludeUnlimited)
            parameter("exclude_limited_upgradable", excludeLimitedUpgradable)
            parameter("exclude_limited_non_upgradable", excludeLimitedNonUpgradable)
            parameter("exclude_unique", excludeUnique)
            parameter("exclude_from_blockchain", excludeFromBlockchain)
            parameter("sort_by_price", sortByPrice)
            parameter("offset", offset)
            parameter("limit", limit)
        }
    }

    override suspend fun getUserGifts(
        userId: Long,
        excludeUnlimited: Boolean?,
        excludeLimitedUpgradable: Boolean?,
        excludeLimitedNonUpgradable: Boolean?,
        excludeFromBlockchain: Boolean?,
        excludeUnique: Boolean?,
        sortByPrice: Boolean?,
        offset: String?,
        limit: Int?
    ): OwnedGifts {
        if (limit != null) require(limit in 1..100) {
            "Limit must be between 1 and 100."
        }

        return call("getUserGifts") {
            parameter("user_id", userId)
            parameter("exclude_unlimited", excludeUnlimited)
            parameter("exclude_limited_upgradable", excludeLimitedUpgradable)
            parameter("exclude_limited_non_upgradable", excludeLimitedNonUpgradable)
            parameter("exclude_from_blockchain", excludeFromBlockchain)
            parameter("exclude_unique", excludeUnique)
            parameter("sort_by_price", sortByPrice)
            parameter("offset", offset)
            parameter("limit", limit)
        }
    }

    override suspend fun getChatGifts(
        chatId: ChatId,
        excludeUnsaved: Boolean?,
        excludeSaved: Boolean?,
        excludeUnlimited: Boolean?,
        excludeLimitedUpgradable: Boolean?,
        excludeLimitedNonUpgradable: Boolean?,
        excludeFromBlockchain: Boolean?,
        excludeUnique: Boolean?,
        sortByPrice: Boolean?,
        offset: String?,
        limit: Int?
    ): OwnedGifts {
        if (limit != null) require(limit in 1..100) {
            "Limit must be between 1 and 100."
        }

        return call("getChatGifts") {
            parameter("chat_id", chatId.toApiParam())
            parameter("exclude_unsaved", excludeUnsaved)
            parameter("exclude_saved", excludeSaved)
            parameter("exclude_unlimited", excludeUnlimited)
            parameter("exclude_limited_upgradable", excludeLimitedUpgradable)
            parameter("exclude_limited_non_upgradable", excludeLimitedNonUpgradable)
            parameter("exclude_unique", excludeUnique)
            parameter("exclude_from_blockchain", excludeFromBlockchain)
            parameter("sort_by_price", sortByPrice)
            parameter("offset", offset)
            parameter("limit", limit)
        }
    }

    // ===== Convert/upgrade/transfer gift methods. =====

    override suspend fun convertGiftToStars(
        businessConnectionId: String,
        ownedGiftId: String
    ): Boolean = call("convertGiftToStars") {
        parameter("business_connection_id", businessConnectionId)
        parameter("owned_gift_id", ownedGiftId)
    }

    override suspend fun upgradeGift(
        businessConnectionId: String,
        ownedGiftId: String,
        keepOriginalDetails: Boolean?,
        starCount: Int?
    ): Boolean = call("upgradeGift") {
        parameter("business_connection_id", businessConnectionId)
        parameter("owned_gift_id", ownedGiftId)
        parameter("keep_original_details", keepOriginalDetails)
        parameter("star_count", starCount)
    }

    override suspend fun transferGift(
        businessConnectionId: String,
        ownedGiftId: String,
        newOwnerChatId: Long,
        starCount: Int?
    ): Boolean = call("transferGift") {
        parameter("business_connection_id", businessConnectionId)
        parameter("owned_gift_id", ownedGiftId)
        parameter("new_owner_chat_id", newOwnerChatId)
        parameter("star_count", starCount)
    }

    // ===== Post/repost/edit/delete story methods. =====

    override suspend fun postStory(
        businessConnectionId: String,
        content: InputStoryContent,
        activePeriod: StoryActivePeriod,
        caption: String?,
        parseMode: ParseMode?,
        captionEntities: List<MessageEntity>?,
        areas: List<StoryArea>?,
        postToChatPage: Boolean?,
        protectContent: Boolean?
    ): Story {
        if (!caption.isNullOrEmpty()) require(caption.length in 0..2048) {
            "Caption must be between 0 and 2048 characters."
        }

        return call("postStory") {
            parameter("business_connection_id", businessConnectionId)
            parameter("content", content)
            parameter("active_period", activePeriod)
            parameter("caption", caption)
            parameter("parse_mode", parseMode)
            parameter("caption_entities", captionEntities)
            parameter("areas", areas)
            parameter("post_to_chat_page", postToChatPage)
            parameter("protect_content", protectContent)
        }
    }

    suspend fun postStory(
        businessConnectionId: String,
        activePeriod: StoryActivePeriod,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        areas: List<StoryArea>? = null,
        postToChatPage: Boolean? = null,
        protectContent: Boolean? = null,
        block: InputStoryContentBuilder.() -> Unit = {}
    ): Story {
        val builder = InputStoryContentBuilder().apply(block)
        return postStory(
            businessConnectionId = businessConnectionId,
            content = builder.build(),
            activePeriod = activePeriod,
            caption = caption,
            parseMode = parseMode,
            captionEntities = captionEntities,
            areas = areas,
            postToChatPage = postToChatPage,
            protectContent = protectContent,
        )
    }

    override suspend fun repostStory(
        businessConnectionId: String,
        fromChatId: Long,
        fromStoryId: Int,
        activePeriod: StoryActivePeriod,
        postToChatPage: Boolean?,
        protectContent: Boolean?
    ): Story = call("repostStory") {
        parameter("business_connection_id", businessConnectionId)
        parameter("from_chat_id", fromChatId)
        parameter("from_story_id", fromStoryId)
        parameter("active_period", activePeriod)
        parameter("post_to_chat_page", postToChatPage)
        parameter("protect_content", protectContent)
    }

    override suspend fun editStory(
        businessConnectionId: String,
        storyId: Int,
        content: InputStoryContent,
        caption: String?,
        parseMode: ParseMode?,
        captionEntities: List<MessageEntity>?,
        areas: List<StoryArea>?
    ): Story {
        if (!caption.isNullOrEmpty()) require(caption.length in 0..2048) {
            "Caption must be between 0 and 2048 characters."
        }

        return call("postStory") {
            parameter("business_connection_id", businessConnectionId)
            parameter("story_id", storyId)
            parameter("content", content)
            parameter("caption", caption)
            parameter("parse_mode", parseMode)
            parameter("caption_entities", captionEntities)
            parameter("areas", areas)
        }
    }

    suspend fun editStory(
        businessConnectionId: String,
        storyId: Int,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        areas: List<StoryArea>? = null,
        block: InputStoryContentBuilder.() -> Unit = {}
    ): Story {
        val builder = InputStoryContentBuilder().apply(block)
        return editStory(
            businessConnectionId = businessConnectionId,
            storyId = storyId,
            content = builder.build(),
            caption = caption,
            parseMode = parseMode,
            captionEntities = captionEntities,
            areas = areas,
        )
    }

    override suspend fun deleteStory(businessConnectionId: String, storyId: Int): Boolean
        = call("deleteStory") {
        parameter("business_connection_id", businessConnectionId)
        parameter("story_id", storyId)
    }

    // ===== Business messages. =====

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