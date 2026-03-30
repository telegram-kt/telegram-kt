package io.telegramkt.api

import io.telegramkt.model.ParseMode
import io.telegramkt.model.chat.ChatId
import io.telegramkt.model.file.File
import io.telegramkt.model.file.input.InputFile
import io.telegramkt.model.keyboard.reply.InlineKeyboardMarkup
import io.telegramkt.model.keyboard.reply.ReplyMarkup
import io.telegramkt.model.keyboard.reply.parameters.ReplyParameters
import io.telegramkt.model.media.input.AlbumableMedia
import io.telegramkt.model.message.Message
import io.telegramkt.model.message.MessageId
import io.telegramkt.model.message.entity.MessageEntity
import io.telegramkt.model.suggested.SuggestedPostParameters
import io.telegramkt.model.update.Update
import io.telegramkt.model.user.User

/**
 * Telegram Bot API client interface.
 *
 * All methods support:
 * - File uploads via [InputFile] (URL, file_id, bytes, or local path)
 * - HTML/Markdown formatting via [ParseMode]
 * - Reply keyboards and inline keyboards via [ReplyMarkup]
 * - Message effects and paid broadcasts (when applicable)
 */
interface TelegramApi {
    suspend fun getMe(): User

    /**
     * Receive incoming updates using long polling.
     *
     * @param offset Identifier of first update to be returned
     * @param limit Number of updates (1-100)
     * @param timeout Timeout in seconds (0-90), default 30
     * @param allowedUpdates List of update types to receive
     */
    suspend fun getUpdates(
        offset: Int? = null,
        limit: Int? = null,
        timeout: Int? = null,
        allowedUpdates: List<String>? = null,
    ): List<Update>

    // ==================== MESSAGES. ====================

    /**
     * Send text message.
     *
     * Example:
     * ```
     * client.sendMessage(
     *     chatId = ChatId.ById(123456),
     *     text = "Hello <b>World</b>",
     *     parseMode = ParseMode.HTML
     * )
     * ```
     */
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

    /**
     * Edit text of existing message.
     * Works for both regular and inline messages.
     */
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

    /**
     * Delete message from chat.
     *
     * @param chatId Chat where message exists
     * @param messageId Message to delete
     * @return True on success
     */
    suspend fun deleteMessage(
        chatId: ChatId,
        messageId: Int,
    ): Boolean

    /**
     * Delete messages from chat.
     *
     * @param chatId Chat where message exists
     * @param messageIds Messages to delete(1-100)
     * @return True on success
     */
    suspend fun deleteMessages(
        chatId: ChatId,
        messageIds: List<Int>,
    ): Boolean

    /**
     * Forward message of any kind.
     * Service messages can't be forwarded.
     *
     * @param fromChatId Original chat
     * @param messageId Message to forward
     */
    suspend fun forwardMessage(
        chatId: ChatId,
        fromChatId: ChatId,
        messageId: Int,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        videoStartTimestamp: Int? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        messageEffectId: String? = null,
        suggestedPostParameters: SuggestedPostParameters? = null,
    ): Message

    /**
     * Forward multiple messages (1-100).
     *
     * Messages are forwarded in order of list.
     *
     * @param messageIds List of 1-100 message IDs in chat fromChatId
     */
    suspend fun forwardMessages(
        chatId: ChatId,
        fromChatId: ChatId,
        messageIds: List<Int>,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
    ): Message

    /**
     * Copy message (send without forward header).
     * Returns MessageId of sent message.
     */
    suspend fun copyMessage(
        chatId: ChatId,
        fromChatId: ChatId,
        messageId: Int,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        videoStartTimestamp: Int? = null,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        showCaptionAboveMedia: Boolean? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        replyParameters: ReplyParameters? = null,
        replyMarkup: ReplyMarkup? = null,
    ): MessageId

    /**
     * Copy multiple messages (1-100).
     */
    suspend fun copyMessages(
        chatId: ChatId,
        fromChatId: ChatId,
        messageIds: List<Int>,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        removeCaption: Boolean? = null,
    ): List<MessageId>

    // ==================== CALLBACKS. ====================

    /**
     * Get file info and prepare for download.
     *
     * After getting file_path, download via:
     * `https://api.telegram.org/file/bot<token>/<file_path>`
     */
    suspend fun getFile(fileId: String) : File

    /**
     * Answer callback query from inline keyboard.
     *
     * @param text Notification text (1-200 chars), shown as alert if showAlert=true
     * @param showAlert Show alert instead of notification
     * @param url URL to open (for login URLs or game URLs)
     * @param cacheTime Cache answer for up to specified seconds
     */
    suspend fun answerCallbackQuery(
        callbackQueryId: String,
        text: String? = null,
        showAlert: Boolean = false,
        url: String? = null,
        cacheTime: Int? = null,
    )

    // ==================== FILES. ====================

    /**
     * Send photo to chat.
     *
     * File sources:
     * - `InputFile.fromId("file_id")` — reuse existing file
     * - `InputFile.fromUrl("https://...")` — download and send
     * - `InputFile.fromPath(path)` — upload local file
     * - `InputFile.fromBytes(bytes, "photo.jpg")` — from memory
     *
     * Example:
     * ```
     * client.sendPhoto(
     *     chatId = chatId,
     *     photo = InputFile.fromUrl("https://example.com/cat.jpg"),
     *     caption = "🐱 Meow!",
     *     parseMode = ParseMode.HTML
     * )
     * ```
     *
     * @param photo Photo to send (max 10MB)
     * @param caption Photo caption (0-1024 chars)
     * @param hasSpoiler Hide media behind spoiler animation
     */
    suspend fun sendPhoto(
        chatId: ChatId,
        photo: InputFile,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        showCaptionAboveMedia: Boolean? = null,
        hasSpoiler: Boolean? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        suggestedPostParameters: SuggestedPostParameters? = null,
        replyParameters: ReplyParameters? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Message

    /**
     * Send audio file (music).
     *
     * For voice messages use [sendVoice].
     *
     * @param audio Audio file (MP3 recommended, max 50MB)
     * @param duration Duration in seconds
     * @param performer Artist name
     * @param title Track title
     * @param thumbnail Thumbnail of album cover (JPEG, max 200KB, 320x320)
     */
    suspend fun sendAudio(
        chatId: ChatId,
        audio: InputFile,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        duration: Int? = null,
        performer: String? = null,
        title: String? = null,
        thumbnail: InputFile? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        suggestedPostParameters: SuggestedPostParameters? = null,
        replyParameters: ReplyParameters? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Message

    /**
     * Send voice message (opus encoded, .ogg).
     *
     * For music files use [sendAudio].
     *
     * @param voice Voice message (OGG_OPUS, max 1MB per minute, max 20MB)
     * @param duration Duration in seconds
     */
    suspend fun sendVoice(
        chatId: ChatId,
        voice: InputFile,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        duration: Int? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        suggestedPostParameters: SuggestedPostParameters? = null,
        replyParameters: ReplyParameters? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Message

    /**
     * Send general file (document).
     *
     * @param document File to send (max 50MB, 20MB for free bots)
     * @param thumbnail Document thumbnail (JPEG, max 200KB, 320x320)
     * @param disableContentTypeDetection Disable automatic content type detection
     */
    suspend fun sendDocument(
        chatId: ChatId,
        document: InputFile,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        thumbnail: InputFile? = null,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        disableContentTypeDetection: Boolean? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        suggestedPostParameters: SuggestedPostParameters? = null,
        replyParameters: ReplyParameters? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Message

    /**
     * Send video file (MP4).
     *
     * @param video Video to send (max 50MB, 20MB for free bots)
     * @param width Video width
     * @param height Video height
     * @param thumbnail Video thumbnail (JPEG, max 200KB)
     * @param cover Cover image for the video
     * @param startTimestamp Start timestamp for the video in the message
     * @param supportsStreaming Streamable video URL sent by Telegram
     */
    suspend fun sendVideo(
        chatId: ChatId,
        video: InputFile,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        duration: Int? = null,
        width: Int? = null,
        height: Int? = null,
        thumbnail: InputFile? = null,
        cover: InputFile? = null,
        startTimestamp: Int? = null,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        showCaptionAboveMedia: Boolean? = null,
        hasSpoiler: Boolean? = null,
        supportsStreaming: Boolean? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        suggestedPostParameters: SuggestedPostParameters? = null,
        replyParameters: ReplyParameters? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Message

    /**
     * Send rounded square video (video message).
     *
     * @param videoNote Video note to send (max 1 minute, 20MB)
     * @param length Video width and height (diameter of the video message), 1-640
     * @param thumbnail Thumbnail of file (JPEG, max 200KB)
     */
    suspend fun sendVideoNote(
        chatId: ChatId,
        videoNote: InputFile,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        duration: Int? = null,
        length: Int? = null,
        thumbnail: InputFile? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        suggestedPostParameters: SuggestedPostParameters? = null,
        replyParameters: ReplyParameters? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Message

    /**
     * Send animation (GIF or H.264/MPEG-4 AVC video without sound).
     *
     * @param animation Animation to send (max 50MB)
     */
    suspend fun sendAnimation(
        chatId: ChatId,
        animation: InputFile,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        duration: Int? = null,
        width: Int? = null,
        height: Int? = null,
        thumbnail: InputFile? = null,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        showCaptionAboveMedia: Boolean? = null,
        hasSpoiler: Boolean? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        suggestedPostParameters: SuggestedPostParameters? = null,
        replyParameters: ReplyParameters? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Message

    suspend fun sendMediaGroup(
        chatId: ChatId,
        media: List<AlbumableMedia>,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        replyParameters: ReplyParameters? = null,
    ): List<Message>

    // ==================== LOCATION & VENUES. ====================

    /**
     * Send point on the map.
     *
     * Example:
     * ```
     * // Simple location
     * client.sendLocation(chatId, 55.7558f, 37.6173f)
     *
     * // Live location with accuracy
     * client.sendLocation(chatId, 55.7558f, 37.6173f) {
     *     horizontalAccuracy = 100f
     *     livePeriod = 3600  // Updates for 1 hour
     * }
     * ```
     *
     * @param latitude Latitude (0-180)
     * @param longitude Longitude (0-180)
     * @param horizontalAccuracy Radius of uncertainty in meters (0-1500)
     * @param livePeriod Time for live updates in seconds (60-86400)
     * @param heading Direction of movement in degrees (1-360)
     * @param proximityAlertRadius Alert radius in meters (1-100000)
     */
    suspend fun sendLocation(
        chatId: ChatId,
        latitude: Float,
        longitude: Float,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        horizontalAccuracy: Float? = null,
        livePeriod: Int? = null,
        heading: Int? = null,
        proximityAlertRadius: Int? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        suggestedPostParameters: SuggestedPostParameters? = null,
        replyParameters: ReplyParameters? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Message

    /**
     * Send venue (location with info).
     *
     * Combines location with title, address and optional provider IDs.
     *
     * Example:
     * ```
     * // Simple venue
     * client.sendVenue(chatId, 55.7558f, 37.6173f, "Kremlin", "Moscow, Russia")
     *
     * // With Google Places data
     * client.sendVenue(chatId, 55.7558f, 37.6173f, "Kremlin", "Moscow, Russia") {
     *     googlePlaceId = "ChIJbQhtR0S3t0kRe0wNZT8iD1g"
     * }
     * ```
     *
     * @param latitude Venue latitude
     * @param longitude Venue longitude
     * @param title Venue name (1-128 chars)
     * @param address Venue address (1-256 chars)
     * @param foursquareId Foursquare identifier
     * @param foursquareType Foursquare type
     * @param googlePlaceId Google Places identifier
     * @param googlePlaceType Google Places type
     */
    suspend fun sendVenue(
        chatId: ChatId,
        latitude: Float,
        longitude: Float,
        title: String,
        address: String,
        foursquareId: String? = null,
        foursquareType: String? = null,
        googlePlaceId: String? = null,
        googlePlaceType: String? = null,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        horizontalAccuracy: Float? = null,
        livePeriod: Int? = null,
        heading: Int? = null,
        proximityAlertRadius: Int? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        suggestedPostParameters: SuggestedPostParameters? = null,
        replyParameters: ReplyParameters? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Message
}