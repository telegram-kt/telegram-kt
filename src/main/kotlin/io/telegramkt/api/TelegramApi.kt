package io.telegramkt.api

import io.telegramkt.model.ParseMode
import io.telegramkt.model.bot.command.BotCommand
import io.telegramkt.model.bot.command.BotCommandScope
import io.telegramkt.model.bot.description.BotDescription
import io.telegramkt.model.bot.description.BotShortDescription
import io.telegramkt.model.bot.name.BotName
import io.telegramkt.model.business.BusinessConnection
import io.telegramkt.model.chat.ChatFullInfo
import io.telegramkt.model.chat.ChatId
import io.telegramkt.model.chat.action.ChatAction
import io.telegramkt.model.chat.administrator.ChatAdministratorRights
import io.telegramkt.model.chat.administrator.ChatPermissions
import io.telegramkt.model.chat.invite.ChatInviteLink
import io.telegramkt.model.chat.member.ChatMember
import io.telegramkt.model.checklist.input.InputChecklist
import io.telegramkt.model.file.File
import io.telegramkt.model.file.input.InputFile
import io.telegramkt.model.forum.ForumTopic
import io.telegramkt.model.forum.topic.TopicIconColor
import io.telegramkt.model.gift.AcceptedGiftTypes
import io.telegramkt.model.gift.Gifts
import io.telegramkt.model.gift.owned.OwnedGifts
import io.telegramkt.model.inline.InlineQueryResult
import io.telegramkt.model.keyboard.button.prepared.PreparedKeyboardButton
import io.telegramkt.model.keyboard.button.prepared.PreparedKeyboardButtonType
import io.telegramkt.model.keyboard.reply.InlineKeyboardMarkup
import io.telegramkt.model.keyboard.reply.ReplyMarkup
import io.telegramkt.model.keyboard.reply.parameters.ReplyParameters
import io.telegramkt.model.mask.MaskPosition
import io.telegramkt.model.media.input.AlbumableMedia
import io.telegramkt.model.media.input.InputMedia
import io.telegramkt.model.media.input.InputProfilePhoto
import io.telegramkt.model.menu.button.MenuButton
import io.telegramkt.model.message.Message
import io.telegramkt.model.message.MessageId
import io.telegramkt.model.message.entity.MessageEntity
import io.telegramkt.model.message.inline.prepared.PreparedInlineMessage
import io.telegramkt.model.poll.Poll
import io.telegramkt.model.poll.PollType
import io.telegramkt.model.poll.input.InputPollOption
import io.telegramkt.model.premium.PremiumSubscriptionPeriod
import io.telegramkt.model.reaction.ReactionType
import io.telegramkt.model.star.StarAmount
import io.telegramkt.model.sticker.Sticker
import io.telegramkt.model.sticker.enums.StickerType
import io.telegramkt.model.sticker.input.InputSticker
import io.telegramkt.model.sticker.input.StickerFormat
import io.telegramkt.model.sticker.set.StickerSet
import io.telegramkt.model.story.InputStoryContent
import io.telegramkt.model.suggested.SuggestedPostParameters
import io.telegramkt.model.update.Update
import io.telegramkt.model.user.User
import io.telegramkt.model.user.boost.UserChatBoosts
import io.telegramkt.model.user.profile.UserProfileAudios
import io.telegramkt.model.user.profile.UserProfilePhotos
import io.telegramkt.model.story.Story
import io.telegramkt.model.story.StoryActivePeriod
import io.telegramkt.model.story.area.StoryArea
import io.telegramkt.model.web.SentWebAppMessage
import kotlin.time.Duration
import kotlin.time.Instant

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

    suspend fun sendMessageDraft(
        chatId: Long,
        draftId: Int,
        text: String,
        messageThreadId: Int? = null,
        parseMode: ParseMode? = null,
        entities: List<MessageEntity>? = null,
    ): Boolean

    /**
     * Edit text of existing message.
     * Works for both regular and inline messages.
     */
    suspend fun editMessageText(
        chatId: ChatId? = null,
        messageId: Int? = null,
        text: String,
        inlineMessageId: String? = null,
        parseMode: ParseMode? = null,
        entities: List<MessageEntity>? = null,
        disableWebPagePreview: Boolean? = null,
        replyMarkup: InlineKeyboardMarkup? = null,
        businessConnectionId: String? = null,
    ): Message

    /**
     * Edit caption of existing message.
     * Works for both regular and inline messages.
     */
    suspend fun editMessageCaption(
        chatId: ChatId?,
        messageId: Int?,
        caption: String? = null,
        parseMode: ParseMode? = null,
        inlineMessageId: String? = null,
        captionEntities: List<MessageEntity>? = null,
        showCaptionAboveMedia: Boolean? = null,
        replyMarkup: InlineKeyboardMarkup? = null,
        businessConnectionId: String? = null,
    ): Message

    suspend fun editMessageMedia(
        chatId: ChatId?,
        messageId: Int?,
        inlineMessageId: String? = null,
        media: InputMedia,
        replyMarkup: InlineKeyboardMarkup? = null,
        businessConnectionId: String? = null,
    ): Message

    suspend fun editMessageReplyMarkup(
        chatId: ChatId?,
        messageId: Int?,
        inlineMessageId: String? = null,
        replyMarkup: InlineKeyboardMarkup?,
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
        cacheTime: Duration? = null,
    ): Boolean

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

    /**
     * Send sticker.
     *
     * @param chatId Chat ID for sending a sticker.
     * @param sticker Sticker to send (Recommended .WEBP, .WEBM, .TGS formats.)
     *
     * client.sendSticker(chatId, InputFile.fromPath("Path/to/sticker/sticker.webp"))
     */
    suspend fun sendSticker(
        chatId: ChatId,
        sticker: InputFile,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        emoji: String? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        suggestedPostParameters: SuggestedPostParameters? = null,
        replyParameters: ReplyParameters? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Message

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
        livePeriod: Duration? = null,
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

    suspend fun editMessageLiveLocation(
        chatId: ChatId?,
        messageId: Int?,
        inlineMessageId: Int? = null,
        latitude: Float?,
        longitude: Float?,
        businessConnectionId: String? = null,
        livePeriod: Duration? = null,
        horizontalAccuracy: Float? = null,
        heading: Int? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Message

    suspend fun stopMessageLiveLocation(
        chatId: ChatId?,
        messageId: Int?,
        inlineMessageId: Int? = null,
        businessConnectionId: String? = null,
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
        livePeriod: Duration? = null,
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

    // ==================== CONTACTS. ====================

    /**
     * Send phone contact.
     *
     * Example:
     * ```
     * client.sendContact(
     *     chatId = chatId,
     *     phoneNumber = "+1-555-123-4567",
     *     firstName = "John",
     *     lastName = "Doe"
     * )
     * ```
     *
     * @param phoneNumber Contact's phone number
     * @param firstName Contact's first name
     * @param lastName Contact's last name
     * @param vCard Additional data about the contact in vCard format (0-2048 bytes)
     */
    suspend fun sendContact(
        chatId: ChatId,
        phoneNumber: String,
        firstName: String,
        lastName: String? = null,
        vCard: String? = null,
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
    ): Message

    // ==================== POOL. ====================

    /**
     * Send native poll.
     *
     * Example:
     * ```
     * // Simple poll
     * client.sendPoll(chatId, "Favorite color?") {
     *     answer("Red")
     *     answer("Green")
     *     answer("Blue")
     * }
     *
     * // Quiz with correct answer
     * client.sendPoll(
     *     chatId = chatId,
     *     question = "2 + 2 = ?",
     *     options = listOf(
     *         InputPollOption("3"),
     *         InputPollOption("4"),
     *         InputPollOption("5")
     *     ),
     *     type = PollType.QUIZ,
     *     correctOptionId = 1,
     *     explanation = "Basic math"
     * )
     * ```
     *
     * @param question Poll question (1-300 chars)
     * @param options List of 2-10 answer options
     * @param isAnonymous True if poll is anonymous
     * @param type Poll type: REGULAR (default) or QUIZ
     * @param allowsMultipleAnswers True if multiple answers allowed (regular only)
     * @param correctOptionId 0-based index of correct answer (quiz only)
     * @param explanation Text shown when user selects wrong answer (quiz only, 0-200 chars)
     * @param openPeriod Time in seconds before poll auto-closes (5-600)
     * @param closeDate Point in time when poll will auto-close
     * @param isClosed True to immediately close the poll
     */
    suspend fun sendPoll(
        chatId: ChatId,
        question: String,
        options: List<InputPollOption>,
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
    ): Message

    suspend fun stopPoll(
        chatId: ChatId,
        messageId: Int,
        replyMarkup: InlineKeyboardMarkup? = null,
        businessConnectionId: String? = null,
    ): Poll

    // ==================== CheckList. ====================

    suspend fun sendChecklist(
        chatId: ChatId,
        checklist: InputChecklist,
        businessConnectionId: String,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        messageEffectId: String? = null,
        replyParameters: ReplyParameters? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Message

    suspend fun editMessageChecklist(
        chatId: ChatId,
        messageId: Int,
        checklist: InputChecklist,
        businessConnectionId: String,
        replyMarkup: InlineKeyboardMarkup? = null,
    ): Message

    // ==================== Dice. ====================

    suspend fun sendDice(
        chatId: ChatId,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        emoji: String? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        suggestedPostParameters: SuggestedPostParameters? = null,
        replyParameters: ReplyParameters? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Message

    // ==================== Chat Action. ====================

    suspend fun sendChatAction(
        chatId: ChatId,
        action: ChatAction,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
    ): Boolean

    // ==================== Reactions. ====================

    suspend fun setMessageReaction(
        chatId: ChatId,
        messageId: Int,
        reaction: List<ReactionType>,
        isBig: Boolean? = null,
    ): Boolean

    // ==================== User Profile. ====================
    suspend fun getUserProfilePhotos(
        userId: Long,
        offset: Int? = null,
        limit: Int? = null,
    ): UserProfilePhotos

    suspend fun getUserProfileAudios(
        userId: Long,
        offset: Int? = null,
        limit: Int? = null,
    ): UserProfileAudios

    suspend fun setUserEmojiStatus(
        userId: Long,
        emojiStatusCustomEmojiId: String? = null,
        emojiStatusExpirationDate: String? = null,
    ): Boolean

    // ==================== Files. ====================

    /**
     * Get file info and prepare for download.
     *
     * After getting file_path, download via:
     * `https://api.telegram.org/file/bot<token>/<file_path>`
     */
    suspend fun getFile(fileId: String): File

    // ==================== Admin Actions. ====================
    // ==================== Ban/unban chat members. ====================
    suspend fun banChatMember(
        chatId: ChatId,
        userId: Long,
        untilDate: Instant? = null,
        revokeMessages: Boolean? = null,
    ): Boolean

    suspend fun unbanChatMember(
        chatId: ChatId,
        userId: Long,
        onlyIfBanned: Boolean? = null,
    ): Boolean

    // ==================== Restrict and promote chat members. ====================

    suspend fun restrictChatMember(
        chatId: ChatId,
        userId: Long,
        permissions: ChatPermissions,
        untilDate: Instant? = null,
        useIndependentChatPermissions: Boolean? = null,
    ): Boolean

    suspend fun promoteChatMember(
        chatId: ChatId,
        userId: Long,
        isAnonymous: Boolean? = null,
        canManageChat: Boolean? = null,
        canDeleteMessages: Boolean? = null,
        canManageVideoChats: Boolean? = null,
        canRestrictMembers: Boolean? = null,
        canPromoteMembers: Boolean? = null,
        canChangeInfo: Boolean? = null,
        canInviteUsers: Boolean? = null,
        canPostStories: Boolean? = null,
        canEditStories: Boolean? = null,
        canDeleteStories: Boolean? = null,
        canPostMessages: Boolean? = null,
        canEditMessages: Boolean? = null,
        canPinMessages: Boolean? = null,
        canManageTopics: Boolean? = null,
        canManageDirectMessages: Boolean? = null,
        canManageTags: Boolean? = null,
    ): Boolean

    // ==================== Set chat administrator custom title. ====================

    suspend fun setChatAdministratorCustomTitle(
        chatId: ChatId,
        userId: Long,
        customTitle: String,
    ): Boolean

    // ==================== Set chat member custom tag. ====================

    suspend fun setChatMemberTag(
        chatId: ChatId,
        userId: Long,
        tag: String,
    ): Boolean

    // ==================== Ban/unban chan sender chat. ====================

    suspend fun banChatSenderChat(
        chatId: ChatId,
        senderChatId: Long,
    ): Boolean

    suspend fun unbanChatSenderChat(
        chatId: ChatId,
        senderChatId: Long,
    ): Boolean

    // ==================== Set chat permissions. ====================

    suspend fun setChatPermissions(
        chatId: ChatId,
        permissions: ChatPermissions,
        useIndependentChatPermissions: Boolean? = null,
    ): Boolean

    // ==================== Chat invite link. ====================
    suspend fun exportChatInviteLink(chatId: ChatId): String?

    suspend fun createChatInviteLink(
        chatId: ChatId,
        name: String? = null,
        expireDate: Instant? = null,
        memberLimit: Int? = null,
        createsJoinRequest: Boolean? = null,
    ): ChatInviteLink

    suspend fun editChatInviteLink(
        chatId: ChatId,
        inviteLink: String,
        name: String? = null,
        expireDate: Instant? = null,
        memberLimit: Int? = null,
        createsJoinRequest: Boolean? = null,
    ): ChatInviteLink

    suspend fun revokeChatInviteLink(
        chatId: ChatId,
        inviteLink: String,
    ): ChatInviteLink

    // ==================== Chat subscription invite link. ====================

    suspend fun createChatSubscriptionInviteLink(
        chatId: ChatId,
        subscriptionPeriod: Duration,
        subscriptionPrice: Int,
        name: String? = null,
    ): ChatInviteLink

    suspend fun editChatSubscriptionInviteLink(
        chatId: ChatId,
        inviteLink: String,
        name: String? = null,
    ): ChatInviteLink

    // ==================== Join requests actions. ====================

    suspend fun approveChatJoinRequest(
        chatId: ChatId,
        userId: Long,
    ): Boolean

    suspend fun declineChatJoinRequest(
        chatId: ChatId,
        userId: Long,
    ): Boolean

    // ==================== Chat photo, title and description. ====================

    suspend fun setChatPhoto(
        chatId: ChatId,
        photo: InputFile,
    ): Boolean

    suspend fun deleteChatPhoto(chatId: ChatId): Boolean

    suspend fun setChatTitle(
        chatId: ChatId,
        title: String,
    ): Boolean

    suspend fun setChatDescription(
        chatId: ChatId,
        description: String,
    ): Boolean

    // ==================== Pin/unpin messages. ====================
    suspend fun pinChatMessage(
        chatId: ChatId,
        messageId: Int,
        businessConnectionId: String? = null,
        disableNotification: Boolean? = null,
    ): Boolean

    suspend fun unpinChatMessage(
        chatId: ChatId,
        messageId: Int,
        businessConnectionId: String? = null,
    ): Boolean

    suspend fun unpinAllChatMessages(chatId: ChatId): Boolean

    // ==================== Leave from chat. ====================

    suspend fun leaveChat(chatId: ChatId): Boolean

    // ==================== Get chat. ====================
    suspend fun getChat(chatId: ChatId): ChatFullInfo

    // ==================== Get chat administrators. ====================
    suspend fun getChatAdministrators(chatId: ChatId): List<ChatMember>

    // ==================== Get chat members. ====================

    suspend fun getChatMemberCount(chatId: ChatId): Int

    suspend fun getChatMember(
        chatId: ChatId,
        userId: Long,
    ): ChatMember

    // ==================== Set/delete chat sticker set. ====================

    suspend fun setChatStickerSet(
        chatId: ChatId,
        stickerSetName: String,
    ): Boolean

    suspend fun deleteChatStickerSet(chatId: ChatId): Boolean

    // ==================== Forum topics. ====================

    suspend fun getForumTopicIconStickers(): List<Sticker>

    suspend fun createForumTopic(
        chatId: ChatId,
        name: String,
        iconColor: TopicIconColor? = null,
        iconCustomEmojiId: String? = null,
    ): ForumTopic

    suspend fun editForumTopic(
        chatId: ChatId,
        messageThreadId: Int,
        name: String? = null,
        iconCustomEmojiId: String? = null,
    ): Boolean

    suspend fun closeForumTopic(
        chatId: ChatId,
        messageThreadId: Int,
    ): Boolean

    suspend fun reopenForumTopic(
        chatId: ChatId,
        messageThreadId: Int,
    ): Boolean

    suspend fun deleteForumTopic(
        chatId: ChatId,
        messageThreadId: Int,
    ): Boolean

    suspend fun unpinAllForumTopicMessages(
        chatId: ChatId,
        messageThreadId: Int,
    ): Boolean

    // ==================== General forum topic. ====================

    suspend fun editGeneralForumTopic(
        chatId: ChatId,
        name: String,
    ): Boolean

    suspend fun closeGeneralForumTopic(chatId: ChatId): Boolean

    suspend fun reopenGeneralForumTopic(chatId: ChatId): Boolean

    suspend fun hideGeneralForumTopic(chatId: ChatId): Boolean

    suspend fun unhideGeneralForumTopic(chatId: ChatId): Boolean

    suspend fun unpinAllGeneralForumTopicMessages(chatId: ChatId): Boolean

    // ==================== User chat boosts. ====================
    suspend fun getUserChatBoosts(
        chatId: ChatId,
        userId: Long,
    ): UserChatBoosts

    // ==================== Business connection. ====================
    suspend fun getBusinessConnection(businessConnectionId: String): BusinessConnection

    // ==================== Managed bot token. ====================
    suspend fun getManagedBotToken(userId: Long): String

    suspend fun replaceManagedBotToken(userId: Long): String

    // ==================== Bot commands. ====================

    suspend fun setMyCommands(
        commands: List<BotCommand>,
        scope: BotCommandScope? = null,
        languageCode: String? = null,
    ): Boolean

    suspend fun deleteMyCommands(
        scope: BotCommandScope? = null,
        languageCode: String? = null,
    ): Boolean

    suspend fun getMyCommands(
        scope: BotCommandScope? = null,
        languageCode: String? = null,
    ): List<BotCommand>

    // ==================== Bot name, description and photo. ====================

    suspend fun setMyName(
        name: String,
        languageCode: String? = null,
    ): Boolean

    suspend fun getMyName(languageCode: String? = null): BotName

    suspend fun setMyDescription(
        description: String,
        languageCode: String? = null,
    ): Boolean

    suspend fun getMyDescription(languageCode: String? = null): BotDescription

    suspend fun setMyShortDescription(
        shortDescription: String,
        languageCode: String? = null,
    ): Boolean

    suspend fun getMyShortDescription(languageCode: String? = null): BotShortDescription

    suspend fun setMyProfilePhoto(
        photo: InputProfilePhoto
    ): Boolean

    suspend fun removeMyProfilePhoto(): Boolean

    // ==================== Chat menu button. ====================

    suspend fun setChatMenuButton(
        chatId: Long? = null,
        menuButton: MenuButton? = null,
    ): Boolean

    suspend fun getChatMenuButton(
        chatId: Long? = null,
    ): MenuButton

    // ==================== Default administrator Rights. ====================

    suspend fun setMyDefaultAdministratorRights(
        rights: ChatAdministratorRights? = null,
        forChannels: Boolean? = null,
    ): Boolean

    suspend fun getMyDefaultAdministratorRights(
        forChannels: Boolean? = null,
    ): ChatAdministratorRights

    // ==================== Gifts. ====================

    suspend fun getAvailableGifts(): Gifts

    suspend fun sendGift(
        giftId: String,
        userId: Long? = null,
        chatId: ChatId? = null,
        payForUpgrade: Boolean? = null,
        text: String? = null,
        textParseMode: ParseMode? = null,
        textEntities: List<MessageEntity>? = null,
    ): Boolean

    suspend fun giftPremiumSubscription(
        userId: Int,
        period: PremiumSubscriptionPeriod,
        text: String? = null,
        textParseMode: ParseMode? = null,
        textEntities: List<MessageEntity>? = null,
    ): Boolean

    // ==================== Verifying users and chats. ====================

    suspend fun verifyUser(
        userId: Long,
        customDescription: String? = null,
    ): Boolean

    suspend fun verifyChat(
        chatId: ChatId,
        customDescription: String? = null,
    ): Boolean

    suspend fun removeUserVerification(userId: Long): Boolean

    suspend fun removeChatVerification(chatId: ChatId): Boolean

    // ==================== Business messages. ====================

    suspend fun readBusinessMessage(
        businessConnectionId: String,
        chatId: Long,
        messageId: Int,
    ): Boolean

    suspend fun deleteBusinessMessages(
        businessConnectionId: String,
        messageIds: List<Long>,
    ): Boolean

    // ==================== Business account. ====================

    suspend fun setBusinessAccountName(
        businessConnectionId: String,
        fistName: String,
        lastName: String? = null,
    ): Boolean

    suspend fun setBusinessAccountUsername(
        businessConnectionId: String,
        username: String? = null,
    ): Boolean

    suspend fun setBusinessAccountBio(
        businessConnectionId: String,
        bio: String? = null,
    ): Boolean

    suspend fun setBusinessAccountProfilePhoto(
        businessConnectionId: String,
        photo: InputProfilePhoto,
        isPublic: Boolean? = null,
    ): Boolean

    suspend fun removeBusinessAccountProfilePhoto(
        businessConnectionId: String,
        isPublic: Boolean? = null,
    ): Boolean

    suspend fun setBusinessAccountGiftSettings(
        businessConnectionId: String,
        showGiftButton: Boolean,
        acceptedGiftTypes: AcceptedGiftTypes,
    ): Boolean

    suspend fun getBusinessAccountStarBalance(businessConnectionId: String): StarAmount

    suspend fun transferBusinessAccountStars(
        businessConnectionId: String,
        starAmount: Int,
    ): Boolean

    suspend fun getBusinessAccountGifts(
        businessConnectionId: String,
        excludeUnsaved: Boolean? = null,
        excludeSaved: Boolean? = null,
        excludeUnlimited: Boolean? = null,
        excludeLimitedUpgradable: Boolean? = null,
        excludeLimitedNonUpgradable: Boolean? = null,
        excludeUnique: Boolean? = null,
        excludeFromBlockchain: Boolean? = null,
        sortByPrice: Boolean? = null,
        offset: String? = null,
        limit: Int? = null,
    ): OwnedGifts

    // ==================== Work with gifts. ====================

    suspend fun getUserGifts(
        userId: Long,
        excludeUnlimited: Boolean? = null,
        excludeLimitedUpgradable: Boolean? = null,
        excludeLimitedNonUpgradable: Boolean? = null,
        excludeFromBlockchain: Boolean? = null,
        excludeUnique: Boolean? = null,
        sortByPrice: Boolean? = null,
        offset: String? = null,
        limit: Int? = null,
    ): OwnedGifts

    suspend fun getChatGifts(
        chatId: ChatId,
        excludeUnsaved: Boolean? = null,
        excludeSaved: Boolean? = null,
        excludeUnlimited: Boolean? = null,
        excludeLimitedUpgradable: Boolean? = null,
        excludeLimitedNonUpgradable: Boolean? = null,
        excludeFromBlockchain: Boolean? = null,
        excludeUnique: Boolean? = null,
        sortByPrice: Boolean? = null,
        offset: String? = null,
        limit: Int? = null,
    ): OwnedGifts

    suspend fun convertGiftToStars(
        businessConnectionId: String,
        ownedGiftId: String,
    ): Boolean

    suspend fun upgradeGift(
        businessConnectionId: String,
        ownedGiftId: String,
        keepOriginalDetails: Boolean? = null,
        starCount: Int? = null,
    ): Boolean

    suspend fun transferGift(
        businessConnectionId: String,
        ownedGiftId: String,
        newOwnerChatId: Long,
        starCount: Int? = null,
    ): Boolean

    // ==================== Stories. ====================

    suspend fun postStory(
        businessConnectionId: String,
        content: InputStoryContent,
        activePeriod: StoryActivePeriod,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        areas: List<StoryArea>? = null,
        postToChatPage: Boolean? = null,
        protectContent: Boolean? = null,
    ): Story

    suspend fun repostStory(
        businessConnectionId: String,
        fromChatId: Long,
        fromStoryId: Int,
        activePeriod: StoryActivePeriod,
        postToChatPage: Boolean? = null,
        protectContent: Boolean? = null,
    ): Story

    suspend fun editStory(
        businessConnectionId: String,
        storyId: Int,
        content: InputStoryContent,
        caption: String? = null,
        parseMode: ParseMode? = null,
        captionEntities: List<MessageEntity>? = null,
        areas: List<StoryArea>? = null,
    ): Story

    suspend fun deleteStory(
        businessConnectionId: String,
        storyId: Int,
    ): Boolean

    // ==================== Answer web app query. ====================
    suspend fun answerWebAppQuery(
        webAppQueryId: String,
        result: InlineQueryResult,
    ): SentWebAppMessage

    // ==================== Save prepared inline message/ keyboard button. ====================
    suspend fun savePreparedInlineMessage(
        userId: Long? = null,
        result: InlineQueryResult,
        allowUserChats: Boolean? = null,
        allowBotChats: Boolean? = null,
        allowGroupChats: Boolean? = null,
        allowChannelChats: Boolean? = null,
    ): PreparedInlineMessage

    suspend fun savePreparedKeyboardButton(
        name: String,
        type: PreparedKeyboardButtonType,
        maxQuantity: Int? = null,
        requestWriteAccess: Boolean? = null,
    ): PreparedKeyboardButton

    // ==================== Suggested posts. ====================
    suspend fun approveSuggestedPost(
        chatId: ChatId,
        messageId: Int,
        sendDate: Instant? = null,
    ): Boolean

    suspend fun declineSuggestedPost(
        chatId: ChatId,
        messageId: Int,
        comment: String? = null,
    ): Boolean

    // ==================== Stickers.. ====================
    suspend fun getStickerSet(name: String): StickerSet

    suspend fun getEmojiCustomStickers(customEmojiIds: List<String>): List<Sticker>

    suspend fun uploadStickerFile(
        userId: Long,
        sticker: InputFile,
        stickerFormat: StickerFormat,
    ): File

    /**
     * Create new sticker set.
     *
     * @param userId User identifier of created sticker set owner.
     * @param name Short name of sticker set to be used in add sticker URLs(t.me/addstickers/)  (1-64 characters).
     * @param title Sticker set title (1-16 characters).
     * @param stickers  List of initial stickers to be added to the sticker set (1-50 items).
     * @param stickerType Optional. Type of stickers in the set. Default: Regular.
     * @param needsRepainting Optional. Should the stickers be recolored to match the text/accent color (Only for custom emoji sticker sets).
     */

    suspend fun createNewStickerSet(
        userId: Long,
        name: String,
        title: String,
        stickers: List<InputSticker>,
        stickerType: StickerType = StickerType.REGULAR,
        needsRepainting: Boolean = false,
    ): Boolean

    /**
     * Add sticker to set.
     *
     * @param userId User identifier of sticker set owner.
     * @param name Sticker set name.
     * @param sticker Object with information about the added sticker.
     */
    suspend fun addStickerToSet(
        userId: Long,
        name: String,
        sticker: InputSticker,
    ): Boolean

    /**
     * Move a sticker in a set created by the bot to a specific position.
     *
     * @param sticker File identifier of the sticker.
     * @param position New sticker position in the set (Zero-based).
     */
    suspend fun setStickerPositionInSet(
        sticker: String,
        position: Int,
    ): Boolean

    /**
     * Delete a sticker from a set.
     *
     * @param sticker File identifier of the sticker.
     */
    suspend fun deleteStickerFromSet(sticker: String): Boolean

    /**
     * Replace an existing sticker in a sticker set with a new one.
     *
     * @param userId User identifier of sticker set owner.
     * @param name Sticker set name.
     * @param oldSticker File identifier of the replaced sticker.
     * @param sticker Object with information about the added sticker.
     */
    suspend fun replaceStickerInSet(
        userId: Long,
        name: String,
        oldSticker: String,
        sticker: InputSticker,
    ): Boolean

    /**
     * Change the list of emoji assigned to a regular or custom emoji sticker.
     *
     * @param sticker File identifier of the replaced sticker.
     * @param emojiList List of emojis associated with the sticker (1-20 items).
     */
    suspend fun setStickerEmojiList(
        sticker: String,
        emojiList: List<String>,
    ): Boolean

    /**
     * Change search keywords assigned to a regular or custom emoji sticker.
     *
     * @param sticker File identifier of the sticker.
     * @param keywords Search keywords for the sticker (0-20 items, up to 64 characters length).
     */
    suspend fun setStickerKeywords(
        sticker: String,
        keywords: List<String>? = null,
    ): Boolean

    /**
     * Change the mask position of a mask sticker.
     *
     * @param sticker File identifier of the sticker.
     * @param maskPosition Optional. Object with the position where the mask should be placed on faces.
     */
    suspend fun setStickerMaskPosition(
        sticker: String,
        maskPosition: MaskPosition? = null,
    ): Boolean

    /**
     * Set the title of a created sticker set.
     *
     * @param name Sticker set name.
     * @param title Sticker set title (1-64 characters).
     */
    suspend fun setStickerSetTitle(
        name: String,
        title: String,
    ): Boolean

    /**
     * Set the thumbnail of a regular or mask sticker set.
     *
     * @param name Sticker set name.
     * @param userId User identifier of the sticker set owner.
     * @param format Format of the thumbnail (StickerFormat was used because of its similar parameters).
     * @param thumbnail Optional. A .WEBP or .PNG image, .TGS animation or .WEBM video with the thumbnail.
     */
    suspend fun setStickerSetThumbnail(
        name: String,
        userId: Long,
        format: StickerFormat,
        thumbnail: InputFile? = null,
    ): Boolean

    /**
     * Set the thumbnail of a custom emoji sticker set.
     *
     * @param name Sticker set name.
     * @param customEmojiId Optional. Custom emoji identifier of a sticker from the sticker set. (Skit this parameter to remove the thumbnail).
     */
    suspend fun setCustomEmojiStickerSetThumbnail(
        name: String,
        customEmojiId: String? = null,
    ): Boolean

    /**
     *  Delete a sticker set that was created by the bot.
     *
     *  @param name Sticker set name.
     */
    suspend fun deleteStickerSet(name: String): Boolean
}