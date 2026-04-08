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
import io.telegramkt.model.chat.administrator.ChatPermissions
import io.telegramkt.model.chat.invite.ChatInviteLink
import io.telegramkt.model.chat.member.ChatMember
import io.telegramkt.model.checklist.input.InputChecklist
import io.telegramkt.model.file.File
import io.telegramkt.model.file.input.InputFile
import io.telegramkt.model.forum.ForumTopic
import io.telegramkt.model.forum.topic.TopicIconColor
import io.telegramkt.model.keyboard.reply.InlineKeyboardMarkup
import io.telegramkt.model.keyboard.reply.ReplyMarkup
import io.telegramkt.model.keyboard.reply.parameters.ReplyParameters
import io.telegramkt.model.media.input.AlbumableMedia
import io.telegramkt.model.media.input.InputProfilePhoto
import io.telegramkt.model.message.Message
import io.telegramkt.model.message.MessageId
import io.telegramkt.model.message.entity.MessageEntity
import io.telegramkt.model.poll.PollType
import io.telegramkt.model.poll.input.InputPollOption
import io.telegramkt.model.reaction.ReactionType
import io.telegramkt.model.sticker.Sticker
import io.telegramkt.model.suggested.SuggestedPostParameters
import io.telegramkt.model.update.Update
import io.telegramkt.model.user.User
import io.telegramkt.model.user.boost.UserChatBoosts
import io.telegramkt.model.user.profile.UserProfileAudios
import io.telegramkt.model.user.profile.UserProfilePhotos
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
}