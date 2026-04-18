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
import io.telegramkt.model.callback.CallbackQuery
import io.telegramkt.model.inline.InlineQueryResult
import io.telegramkt.model.keyboard.button.prepared.PreparedKeyboardButton
import io.telegramkt.model.keyboard.button.prepared.PreparedKeyboardButtonType
import io.telegramkt.model.keyboard.reply.InlineKeyboardMarkup
import io.telegramkt.model.keyboard.reply.ReplyMarkup
import io.telegramkt.model.keyboard.reply.parameters.ReplyParameters
import io.telegramkt.model.mask.MaskPosition
import io.telegramkt.model.media.input.AlbumableMedia
import io.telegramkt.model.media.input.InputMedia
import io.telegramkt.model.media.input.InputMediaPhoto
import io.telegramkt.model.media.input.InputMediaVideo
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
import io.telegramkt.exception.TelegramApiException
import io.telegramkt.model.keyboard.inline.InlineQueryResultsButton
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
     * Send a text message to a chat.
     *
     * Samples:
     * ```kotlin
     * // Simple text
     * bot.sendMessage(ChatId.ById(123456), "Hello!")
     *
     * // With HTML formatting
     * bot.sendMessage(
     *      chatId = ChatId.ByUsername("@channel"),
     *      text = "Check this: <a href=\"https://example.com\">link</a>",
     *      parseMode = ParseMode.HTML
     * )
     *
     * // With custom entities (more control than parseMode)
     * bot.sendMessage(
     *      chatId = ChatId.ById(123456),
     *      text = "Hello bold world",
     *      entities = listOf(
     *          MessageEntity(type = "bold", offset = 6, length = 4)
     *      )
     * )
     *```
     *
     * Supports HTML, MarkdownV2, and plain text formatting via [parseMode].
     * For advanced formatting, use [entities] instead of [parseMode].
     *
     * @param chatId Unique identifier for the target chat or username (@channel)
     * @param text Text of the message to be sent (1-4096 characters after entities parsing)
     * @param parseMode Mode for parsing entities: [ParseMode.HTML], [ParseMode.MARKDOWN_V2], or null for plain text
     * @param entities List of special entities (bold, italic, links, etc.). If specified, [parseMode] is ignored
     * @param disableWebPagePreview Disables link previews for links in this message
     * @param disableNotification Sends the message silently (users receive no notification)
     * @param protectContent Protects the contents of the sent message from forwarding and saving
     * @param replyToMessageId If the message is a reply, ID of the original message
     * @param allowSendingWithoutReply Pass True if the message should be sent even if the replied-to message is deleted
     * @param replyMarkup Additional interface options: [InlineKeyboardMarkup], [io.telegramkt.model.keyboard.reply.ReplyKeyboardMarkup], etc.
     * @param businessConnectionId Unique identifier of the business connection (for business accounts)
     * @param messageEffectId Unique identifier of the message effect to apply
     * @return The sent [Message]
     *
     * @throws TelegramApiException if:
     *   - chat not found
     *   - text is empty or too long
     *   - bot has no rights to send messages
     *
     * @see editMessageText
     * @see sendPhoto
     * @see answerCallbackQuery
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
     * Save a message as a draft in a chat.
     *
     * Samples:
     * ```kotlin
     * // Save a draft with HTML formatting
     * client.sendMessageDraft(
     *     chatId = 123456L,
     *     draftId = "welcome_msg",
     *     text = "Hello <b>{name}</b>! Use /start to begin.",
     *     parseMode = ParseMode.HTML
     * )
     *
     * // Later: send the draft (implementation depends on your logic)
     * // client.sendSavedDraft(chatId, "welcome_msg")
     * ```
     *
     * Drafts are visible only to the bot and can be edited or sent later.
     * Useful for pre-filling messages in business accounts or scheduled posts.
     *
     * @param chatId Unique identifier for the target chat
     * @param draftId Unique identifier for this draft (1-64 characters)
     * @param text Text of the draft message (1-4096 characters)
     * @param messageThreadId Identifier of the message thread (for supergroups)
     * @param parseMode Formatting mode: [ParseMode.HTML], [ParseMode.MARKDOWN_V2], or null
     * @param entities List of special entities (bold, italic, links). If specified, [parseMode] is ignored
     * @return True if draft was saved successfully
     *
     * @throws TelegramApiException if:
     *   - chat not found or draftId already exists
     *   - text is empty or too long
     *   - bot has no rights to save drafts
     *
     * @see sendMessage
     * @see editMessageText
     */
    suspend fun sendMessageDraft(
        chatId: Long,
        draftId: Int,
        text: String,
        messageThreadId: Int? = null,
        parseMode: ParseMode? = null,
        entities: List<MessageEntity>? = null,
    ): Boolean

    /**
     * Edit text or game message of an existing message.
     *
     * Samples:
     * ```kotlin
     * // Edit a regular message
     * client.editMessageText(
     *     chatId = ChatId.ById(123456),
     *     messageId = 789,
     *     text = "✅ Updated: Order #123 shipped!",
     *     parseMode = ParseMode.HTML
     * )
     *
     * // Edit an inline message (via callback query)
     * client.editMessageText(
     *     inlineMessageId = callbackQuery.inlineMessageId,
     *     text = "🔄 Loading...",
     *     replyMarkup = InlineKeyboardMarkup(...)
     * )
     * ```
     *
     * Works for messages sent by the bot in:
     * • Private chats
     * • Group chats (if bot has edit rights)
     * • Channel posts (if bot is admin)
     * • Inline messages (via [inlineMessageId])
     *
     * Cannot edit messages older than 48 hours, or messages with media (use [editMessageCaption] or [editMessageMedia]).
     *
     * @param chatId Target chat ID (required for regular messages, null for inline)
     * @param messageId ID of message to edit (required for regular messages, null for inline)
     * @param inlineMessageId Identifier of inline message (required if chatId/messageId not provided)
     * @param text New text of the message (1-4096 characters after entities parsing)
     * @param parseMode Formatting mode: [ParseMode.HTML], [ParseMode.MARKDOWN_V2], or null for plain text
     * @param entities List of special entities. If specified, [parseMode] is ignored
     * @param disableWebPagePreview Disable link previews in this message
     * @param replyMarkup New inline keyboard for the message (null to remove keyboard)
     * @param businessConnectionId Unique identifier of the business connection (for business accounts)
     * @return The edited [Message] (or [Boolean] for inline messages)
     *
     * @throws TelegramApiException if:
     *   - message not found or cannot be edited
     *   - text is empty or too long
     *   - bot has no edit rights in chat
     *   - message is older than 48 hours
     *
     * @see sendMessage
     * @see editMessageCaption
     * @see editMessageMedia
     * @see deleteMessage
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
     * Edit caption of a message with media (photo, video, document, etc.).
     *
     * Samples:
     * ```kotlin
     * // Update caption with new info
     * client.editMessageCaption(
     *     chatId = chatId,
     *     messageId = messageId,
     *     caption = "📸 Photo by @user\n👍 Likes: 42",
     *     parseMode = ParseMode.HTML,
     *     showCaptionAboveMedia = true
     * )
     *
     * // Remove caption entirely
     * client.editMessageCaption(
     *     chatId = chatId,
     *     messageId = messageId,
     *     caption = ""  // empty string removes caption
     * )
     * ```
     *
     * Works for messages sent by the bot in private chats, groups, channels, or inline results.
     * Cannot edit captions of messages sent by other users or older than 48 hours.
     *
     * @param chatId Target chat ID (required for regular messages, null for inline)
     * @param messageId ID of message to edit (required for regular messages, null for inline)
     * @param inlineMessageId Identifier of inline message (required if chatId/messageId not provided)
     * @param caption New caption text (0-1024 characters). Pass empty string to remove caption
     * @param parseMode Formatting mode for caption: [ParseMode.HTML], [ParseMode.MARKDOWN_V2], or null
     * @param captionEntities List of special entities for caption. If specified, [parseMode] is ignored
     * @param showCaptionAboveMedia Show caption above media instead of below (for photos/videos)
     * @param replyMarkup New inline keyboard (null to remove, keep existing if not specified)
     * @param businessConnectionId Unique identifier of the business connection
     * @return The edited [Message] (or [Boolean] for inline messages)
     *
     * @throws TelegramApiException if:
     *   - message not found or has no media
     *   - caption is too long (>1024 chars)
     *   - bot has no edit rights
     *
     * @see sendPhoto
     * @see editMessageText
     * @see editMessageMedia
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

    /**
     * Replace media in an existing message (photo → video, document → photo, etc.).
     *
     * Samples:
     * ```kotlin
     * // Replace photo with another photo (by file_id)
     * val newMedia = InputMediaPhoto(
     *     media = InputFile.fromId("AgACAgIA..."),
     *     caption = "🔄 Updated photo!",
     *     parseMode = ParseMode.HTML
     * )
     * client.editMessageMedia(
     *     chatId = chatId,
     *     messageId = messageId,
     *     media = newMedia
     * )
     *
     * // Replace via URL
     * val newMedia = InputMediaPhoto(
     *     media = InputFile.fromUrl("https://example.com/new.jpg"),
     *     caption = "Fresh from the web!"
     * )
     * ```
     *
     * Works for messages with media sent by the bot. The new media must be of a compatible type
     * (e.g., photo ↔ photo, video ↔ video). Cannot change message type (e.g., text → photo).
     *
     * [InputMedia] must use `file_id`/ `https://` URL/local files (`fromPath`, `fromBytes`)
     *
     *
     * @param chatId Target chat ID (required for regular messages, null for inline)
     * @param messageId ID of message to edit (required for regular messages, null for inline)
     * @param inlineMessageId Identifier of inline message (required if chatId/messageId not provided)
     * @param media New media to replace the old one ([InputMediaPhoto], [InputMediaVideo], etc.)
     * @param replyMarkup New inline keyboard (null to keep existing)
     * @param businessConnectionId Unique identifier of the business connection
     * @return The edited [Message] (or [Boolean] for inline messages)
     *
     * @throws TelegramApiException if:
     *   - message not found or has no media
     *   - media type is incompatible with original
     *   - media URL/file_id is invalid
     *   - bot has no edit rights
     *
     * @see sendPhoto
     * @see editMessageCaption
     * @see InputMedia
     */
    suspend fun editMessageMedia(
        chatId: ChatId?,
        messageId: Int?,
        inlineMessageId: String? = null,
        media: InputMedia,
        replyMarkup: InlineKeyboardMarkup? = null,
        businessConnectionId: String? = null,
    ): Message

    /**
     * Edit only the inline keyboard of a message (without changing text/media).
     *
     * Samples:
     * ```kotlin
     * // Update button text after click
     * val newKeyboard = inlineKeyboard {
     *     row(
     *         callback("✅ Done", "done")
     *     )
     * }
     *
     * client.editMessageReplyMarkup(
     *     chatId = chatId,
     *     messageId = messageId,
     *     replyMarkup = newKeyboard
     * )
     *
     * // Remove keyboard entirely
     * client.editMessageReplyMarkup(
     *     chatId = chatId,
     *     messageId = messageId,
     *     replyMarkup = null  // removes keyboard
     * )
     * ```
     *
     * Useful for dynamic buttons: progress indicators, pagination, multi-step forms.
     * Works for messages sent by the bot in any chat type or inline messages.
     *
     * @param chatId Target chat ID (required for regular messages, null for inline)
     * @param messageId ID of message to edit (required for regular messages, null for inline)
     * @param inlineMessageId Identifier of inline message (required if chatId/messageId not provided)
     * @param replyMarkup New inline keyboard, or null to remove keyboard entirely
     * @param businessConnectionId Unique identifier of the business connection
     * @return The edited [Message] (or [Boolean] for inline messages)
     *
     * @throws TelegramApiException if:
     *   - message not found or has no keyboard
     *   - bot has no edit rights
     *   - message is older than 48 hours
     *
     * @see answerCallbackQuery
     * @see editMessageText
     * @see InlineKeyboardMarkup
     */
    suspend fun editMessageReplyMarkup(
        chatId: ChatId?,
        messageId: Int?,
        inlineMessageId: String? = null,
        replyMarkup: InlineKeyboardMarkup?,
        businessConnectionId: String? = null,
    ): Message

    /**
     * Delete a message from a chat.
     *
     * Samples:
     * ```kotlin
     * // Delete a message after processing
     * val msg = client.sendMessage(chatId, "Processing...")
     * doWork()
     * client.deleteMessage(chatId, msg.id)
     *
     * // Delete user message in admin bot (if permitted)
     * onCommand("del") { args ->
     *     val targetMsgId = args.firstOrNull()?.toIntOrNull()
     *     if (targetMsgId != null && isAdmin(ctx)) {
     *         client.deleteMessage(chatId!!, targetMsgId)
     *         reply("🗑️ Deleted")
     *     }
     * }
     * ```
     *
     * Works for:
     * • Messages sent by the bot (always deletable)
     * • Messages in private chats (bot can delete its own)
     * • Messages in groups/channels (if bot has delete rights)
     * • Service messages (only if bot is admin)
     *
     * Cannot delete messages older than 48 hours in groups (Telegram limitation).
     *
     * @param chatId Chat where the message exists
     * @param messageId ID of the message to delete
     * @return True if message was deleted successfully
     *
     * @throws TelegramApiException if:
     *   - message or chat not found
     *   - bot has no delete rights
     *   - message is too old to delete
     *
     * @see deleteMessages
     * @see editMessageText
     */
    suspend fun deleteMessage(
        chatId: ChatId,
        messageId: Int,
    ): Boolean

    /**
     * Delete multiple messages (1-100) from a chat in a single request.
     *
     * More efficient than calling [deleteMessage] in a loop.
     * Messages are deleted in order; if one fails, others still proceed.
     *
     * Same limitations as [deleteMessage]: bot can always delete its own messages,
     * but needs admin rights to delete others' messages in groups.
     *
     * @param chatId Chat where messages exist
     * @param messageIds List of message IDs to delete (must contain 1-100 unique IDs)
     * @return True if all messages were deleted successfully
     *
     * @throws IllegalArgumentException if messageIds is empty or >100 items
     * @throws TelegramApiException if:
     *   - chat not found
     *   - bot lacks delete rights for any message
     *   - any message is too old to delete
     *
     * Samples:
     * ```kotlin
     * // Bulk delete bot's own messages
     * val messageIds = listOf(101, 102, 103, 104)
     * client.deleteMessages(chatId, messageIds)
     *
     * // Clean up after command with error handling
     * try {
     *     client.deleteMessages(chatId, idsToDelete)
     * } catch (e: TelegramApiException) {
     *     println("Partial delete: ${e.message}")
     *     // Handle which messages failed if needed
     * }
     * ```
     *
     * @see deleteMessage
     * @see forwardMessages
     * @see copyMessages
     */
    suspend fun deleteMessages(
        chatId: ChatId,
        messageIds: List<Int>,
    ): Boolean

    /**
     * Forward a message of any kind from one chat to another.
     *
     * Samples:
     * ```kotlin
     * // Simple forward
     * client.forwardMessage(
     *     chatId = ChatId.ById(adminChatId),
     *     fromChatId = ChatId.ById(userChatId),
     *     messageId = reportedMessageId
     * )
     *
     * // Forward with protection (anti-leak)
     * client.forwardMessage(
     *     chatId = privateChatId,
     *     fromChatId = sourceChatId,
     *     messageId = messageId,
     *     protectContent = true  // users can't forward/save this
     * )
     * ```
     *
     * Forwarded messages retain:
     * • Original sender info ("Forwarded from @username")
     * • Media, captions, formatting
     * • Reply markup (inline keyboards)
     *
     * Service messages (pinned, chat changes, etc.) cannot be forwarded.
     * Messages from secret chats cannot be forwarded at all.
     *
     * @param chatId Target chat where message will be forwarded
     * @param fromChatId Source chat where message currently exists
     * @param messageId ID of the message to forward
     * @param messageThreadId Thread ID in target chat (for supergroups with topics)
     * @param directMessagesTopicId Topic ID for DMs with business accounts
     * @param videoStartTimestamp Start timestamp for video messages (in seconds)
     * @param disableNotification Send silently (no notification to users)
     * @param protectContent Prevent forwarding/saving of the forwarded message
     * @param messageEffectId Message effect to apply (premium feature)
     * @param suggestedPostParameters Scheduling options for channel posts
     * @return The forwarded [Message] in the target chat
     *
     * @throws TelegramApiException if:
     *   - source or target chat not found
     *   - message cannot be forwarded (service message, secret chat)
     *   - bot lacks send rights in target chat
     *
     * @see forwardMessages
     * @see copyMessage
     * @see Message
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
     * Forward multiple messages (1-100) from one chat to another in a single request.
     *
     * Samples:
     * ```kotlin
     * // Forward a conversation snippet to admin
     * val messageIds = listOf(100, 101, 102)  // consecutive messages
     * client.forwardMessages(
     *     chatId = adminChatId,
     *     fromChatId = userChatId,
     *     messageIds = messageIds,
     *     protectContent = true
     * )
     *
     * // Bulk forward with error handling
     * try {
     *     client.forwardMessages(chatId, fromChatId, ids)
     * } catch (e: TelegramApiException) {
     *     println("Forward failed: ${e.message}")
     *     // Consider falling back to individual forwards if needed
     * }
     * ```
     *
     * More efficient than looping [forwardMessage]. Messages preserve their order
     * and original metadata (sender, timestamp, media).
     *
     * If any message fails to forward (e.g., service message), the entire request
     * may fail depending on Telegram API behavior — handle errors accordingly.
     *
     * @param chatId Target chat for forwarded messages
     * @param fromChatId Source chat containing messages to forward
     * @param messageIds List of message IDs to forward (1-100 unique IDs, in desired order)
     * @param messageThreadId Thread ID in target chat (for supergroup topics)
     * @param directMessagesTopicId Topic ID for business DMs
     * @param disableNotification Send all messages silently
     * @param protectContent Prevent forwarding/saving of all forwarded messages
     * @return The [Message] object of the last forwarded message (Telegram API behavior)
     *
     * @throws IllegalArgumentException if messageIds is empty or >100 items
     * @throws TelegramApiException if:
     *   - any message cannot be forwarded
     *   - bot lacks rights in source or target chat
     *
     * @see forwardMessage
     * @see copyMessages
     * @see deleteMessages
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
     * Copy a message to another chat without the "Forwarded from" header.
     *
     * Samples:
     * ```kotlin
     * // Copy with new caption and keyboard
     * client.copyMessage(
     *     chatId = publicChannelId,
     *     fromChatId = privateChatId,
     *     messageId = messageId,
     *     caption = "📢 From our community:\n\n{original_caption}",
     *     parseMode = ParseMode.HTML,
     *     replyMarkup = InlineKeyboardMarkup(
     *         listOf(listOf(InlineKeyboardButton("👍 Vote", callbackData = "vote:123")))
     *     )
     * )
     *
     * // Anonymous repost (no original sender info)
     * client.copyMessage(
     *     chatId = broadcastChannel,
     *     fromChatId = userChat,
     *     messageId = messageId,
     *     caption = "🗞️ Community submission",  // original caption replaced
     *     protectContent = true  // prevent further sharing
     * )
     * ```
     *
     * The copied message appears as if sent directly by the bot:
     * • No "Forwarded from @username" label
     * • Bot can modify caption, add keyboard, change formatting
     * • Original sender info is lost (unless manually added to caption)
     *
     * Useful for: content aggregation, moderated reposts, anonymized sharing.
     *
     * @param chatId Target chat where message will be copied
     * @param fromChatId Source chat containing the original message
     * @param messageId ID of the message to copy
     * @param messageThreadId Thread ID in target chat (for supergroup topics)
     * @param directMessagesTopicId Topic ID for business DMs
     * @param videoStartTimestamp Start timestamp for video messages
     * @param caption New caption for the copied message (overrides original)
     * @param parseMode Formatting mode for new caption
     * @param captionEntities Entities for new caption (overrides parseMode if specified)
     * @param showCaptionAboveMedia Show caption above media (for photos/videos)
     * @param disableNotification Send silently
     * @param protectContent Prevent forwarding/saving of the copied message
     * @param allowPaidBroadcast Allow sending to paid broadcast channels
     * @param messageEffectId Message effect to apply
     * @param replyParameters Reply configuration (quote, etc.)
     * @param replyMarkup Inline/reply keyboard for the copied message
     * @return [MessageId] of the newly created message in target chat
     *
     * @throws TelegramApiException if:
     *   - source or target chat not found
     *   - message cannot be copied (service message, restricted content)
     *   - bot lacks send rights in target chat
     *
     * @see forwardMessage
     * @see copyMessages
     * @see sendMessage
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
     * Copy multiple messages (1-100) to another chat without "Forwarded from" headers.
     *
     * Samples:
     * ```kotlin
     * // Bulk anonymized repost
     * val messageIds = listOf(200, 201, 202)
     * val results = client.copyMessages(
     *     chatId = publicChannel,
     *     fromChatId = privateChat,
     *     messageIds = messageIds,
     *     removeCaption = true,  // strip original captions
     *     protectContent = true
     * )
     * println("Copied ${results.size} messages, first ID: ${results.firstOrNull()?.messageId}")
     *
     * // Copy with global caption prefix
     * // (Note: per-message captions require individual copyMessage calls)
     * ```
     *
     * Efficient batch version of [copyMessage]. Each message is copied independently
     * with optional global parameters (caption, keyboard, etc.).
     *
     * Original sender info is lost for all copied messages.
     * If per-message customization is needed, use individual [copyMessage] calls.
     *
     * @param chatId Target chat for copied messages
     * @param fromChatId Source chat containing messages to copy
     * @param messageIds List of message IDs to copy (1-100 unique IDs, in order)
     * @param messageThreadId Thread ID in target chat (for supergroup topics)
     * @param directMessagesTopicId Topic ID for business DMs
     * @param disableNotification Send all messages silently
     * @param protectContent Prevent forwarding/saving of all copied messages
     * @param removeCaption Remove captions from all copied media messages
     * @return List of [MessageId] for each newly created message (in same order as input)
     *
     * @throws IllegalArgumentException if messageIds is empty or >100 items
     * @throws TelegramApiException if:
     *   - any message cannot be copied
     *   - bot lacks rights in source or target chat
     *
     * @see copyMessage
     * @see forwardMessages
     * @see deleteMessages
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
     * Send a response to a callback query from an inline keyboard button.
     *
     * Samples:
     * ```kotlin
     * // Simple acknowledgment toast
     * onCallback("buy:") { data ->
     *     answerCallback(text = "✅ Added to cart", cacheTime = 60)
     * }
     *
     * // Alert modal for important action
     * onCallback("delete:") { data ->
     *     answerCallback(
     *         text = "⚠️ This action cannot be undone!",
     *         showAlert = true
     *     )
     * }
     *
     * // Open URL for OAuth flow
     * onCallback("login") {
     *     answerCallback(
     *         url = "https://example.com/auth?token=xyz",
     *         cacheTime = 300.seconds
     *     )
     * }
     * ```
     *
     * This method is **required** when a user presses an inline keyboard button with `callback_data`.
     * Without calling this, the user will see a loading indicator indefinitely.
     *
     * Must be called within ~30 seconds of receiving the callback query, or Telegram will auto-expire it.
     *
     * @param callbackQueryId Unique identifier for the query (from [CallbackQuery.id])
     * @param text Text of the notification (1-200 characters). If empty, no notification is shown
     * @param showAlert If true, show text as a modal alert; if false, show as a subtle toast notification
     * @param url URL to be opened in the user's browser (e.g., for game auth or login flows)
     * @param cacheTime The maximum amount of time (in seconds) to cache the answer on Telegram servers.
     *                  Reduces repeated callbacks for the same button (useful for static actions).
     * @return True if the answer was sent successfully
     *
     * @throws TelegramApiException if:
     *   - callbackQueryId is invalid or expired
     *   - text is longer than 200 characters
     *   - bot is not a member of the chat (for certain callback types)
     *
     * @see CallbackQuery
     * @see InlineKeyboardMarkup
     * @see editMessageReplyMarkup
     */
    suspend fun answerCallbackQuery(
        callbackQueryId: String,
        text: String? = null,
        showAlert: Boolean = false,
        url: String? = null,
        cacheTime: Duration? = null,
    ): Boolean

    suspend fun answerInlineQuery(
        inlineQueryId: String,
        results: List<InlineQueryResult>,
        cacheTime: Duration? = null,
        isPersonal: Boolean? = null,
        nextOffset: String? = null,
        button: InlineQueryResultsButton? = null,
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
     * Samples:
     * ```kotlin
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
     * Send an audio file (music track) to a chat.
     *
     * Samples:
     * ```kotlin
     * // Send MP3 with metadata
     * client.sendAudio(
     *     chatId = chatId,
     *     audio = InputFile.fromPath("/music/track.mp3"),
     *     title = "Summer Vibes",
     *     performer = "DJ Example",
     *     duration = 3.minutes,
     *     thumbnail = InputFile.fromUrl("https://example.com/cover.jpg"),
     *     caption = "🎵 New release!",
     *     parseMode = ParseMode.HTML
     * )
     *
     * // Send from URL (Telegram downloads it)
     * client.sendAudio(
     *     chatId = chatId,
     *     audio = InputFile.fromUrl("https://cdn.example.com/podcast.ogg"),
     *     title = "Episode 42",
     *     performer = "My Podcast"
     * )
     * ```
     *
     * Telegram displays audio with:
     * • Album art (if [thumbnail] provided)
     * • Title, performer, duration metadata
     * • In-chat audio player with seek/controls
     *
     * For voice messages (recorded speech), use [sendVoice] instead.
     *
     * File requirements:
     * • Format: MP3 recommended (other formats may work but not guaranteed)
     * • Max size: 50 MB (20 MB for free bots)
     * • Duration: No hard limit, but long files may be rejected
     *
     * @param chatId Unique identifier for the target chat or username (@channel)
     * @param audio Audio file to send ([InputFile] abstraction)
     * @param businessConnectionId Unique identifier of the business connection
     * @param messageThreadId Identifier of the message thread (for supergroups with topics)
     * @param directMessagesTopicId Topic ID for business DMs
     * @param caption Caption for the audio message (0-1024 characters)
     * @param parseMode Formatting mode for caption: [ParseMode.HTML], [ParseMode.MARKDOWN_V2], or null
     * @param captionEntities List of special entities for caption. If specified, [parseMode] is ignored
     * @param duration Duration of the audio. (optional, but recommended for proper display)
     * @param performer Artist/performer name (displayed in player)
     * @param title Track title (displayed in player)
     * @param thumbnail Album cover thumbnail (JPEG, max 200KB, recommended 320x320)
     * @param disableNotification Send silently (no notification to users)
     * @param protectContent Prevent forwarding/saving of the sent audio
     * @param allowPaidBroadcast Allow sending to paid broadcast channels
     * @param messageEffectId Message effect to apply (Premium feature)
     * @param suggestedPostParameters Scheduling options for channel posts
     * @param replyParameters Reply configuration (quote, etc.)
     * @param replyMarkup Inline/reply keyboard attached to the message
     * @return The sent [Message] containing the audio
     *
     * @throws TelegramApiException if:
     *   - chat not found or bot has no send rights
     *   - audio file is invalid or unsupported format
     *   - thumbnail is invalid (wrong format/size)
     *   - file size exceeds limits
     *
     * @see sendVoice
     * @see sendVideo
     * @see sendDocument
     * @see InputFile
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
        duration: Duration? = null,
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
     * Send a voice message (recorded speech) to a chat.
     *
     * Samples:
     * ```kotlin
     * // Send pre-recorded voice message
     * client.sendVoice(
     *     chatId = chatId,
     *     voice = InputFile.fromPath("/voice/greeting.ogg"),
     *     duration = 15.seconds,
     *     caption = "👋 Welcome message"
     * )
     *
     * // Send generated voice (e.g., TTS)
     * val ttsBytes = textToSpeech("Hello!") // your TTS logic
     * client.sendVoice(
     *     chatId = chatId,
     *     voice = InputFile.fromBytes(ttsBytes, "tts.ogg"),
     *     caption = "🤖 Bot says:"
     * )
     * ```
     *
     * Voice messages are displayed as:
     * • Waveform player with play/pause/seek
     * • Duration label
     * • "Voice message" badge (distinct from audio files)
     *
     * For music/audio files, use [sendAudio] instead.
     *
     * File requirements:
     * • Format: OGG_OPUS (required by Telegram)
     * • Sample rate: 48 kHz recommended
     * • Channels: Mono
     * • Max size: 20 MB (or ~1 MB per minute of audio)
     *
     * @param chatId Unique identifier for the target chat or username (@channel)
     * @param voice Voice message to send ([InputFile] abstraction, must be OGG_OPUS)
     * @param businessConnectionId Unique identifier of the business connection
     * @param messageThreadId Identifier of the message thread (for supergroups with topics)
     * @param directMessagesTopicId Topic ID for business DMs
     * @param caption Caption for the voice message (0-1024 characters)
     * @param parseMode Formatting mode for caption: [ParseMode.HTML], [ParseMode.MARKDOWN_V2], or null
     * @param captionEntities List of special entities for caption. If specified, [parseMode] is ignored
     * @param duration Voice duration. (optional, but helps with proper display)
     * @param disableNotification Send silently (no notification to users)
     * @param protectContent Prevent forwarding/saving of the voice message
     * @param allowPaidBroadcast Allow sending to paid broadcast channels
     * @param messageEffectId Message effect to apply (Premium feature)
     * @param suggestedPostParameters Scheduling options for channel posts
     * @param replyParameters Reply configuration (quote, etc.)
     * @param replyMarkup Inline/reply keyboard attached to the message
     * @return The sent [Message] containing the voice message
     *
     * @throws TelegramApiException if:
     *   - chat not found or bot has no send rights
     *   - voice file is not OGG_OPUS or is corrupted
     *   - file size exceeds limits
     *
     * @see sendAudio
     * @see sendVideoNote
     * @see InputFile
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
        duration: Duration? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        allowPaidBroadcast: Boolean? = null,
        messageEffectId: String? = null,
        suggestedPostParameters: SuggestedPostParameters? = null,
        replyParameters: ReplyParameters? = null,
        replyMarkup: ReplyMarkup? = null,
    ): Message

    /**
     * Send a general file (document) to a chat.
     *
     * Samples:
     * ```kotlin
     * // Send PDF with thumbnail
     * client.sendDocument(
     *     chatId = chatId,
     *     document = InputFile.fromPath("/docs/manual.pdf"),
     *     thumbnail = InputFile.fromUrl("https://example.com/pdf-icon.jpg"),
     *     caption = "📄 User manual v2.1",
     *     parseMode = ParseMode.HTML
     * )
     *
     * // Send ZIP archive (disable content detection to avoid preview)
     * client.sendDocument(
     *     chatId = chatId,
     *     document = InputFile.fromPath("/archive/data.zip"),
     *     caption = "📦 Exported data",
     *     disableContentTypeDetection = true
     * )
     *
     * // Send from memory (e.g., generated report)
     * val pdfBytes = generateReport() // your logic
     * client.sendDocument(
     *     chatId = chatId,
     *     document = InputFile.fromBytes(pdfBytes, "report.pdf"),
     *     caption = "📊 Your monthly report"
     * )
     * ```
     *
     * Documents are displayed with:
     * • File name and icon based on MIME type
     * • File size label
     * • Download button
     * • Optional caption and keyboard
     *
     * Supported use cases:
     * • PDFs, ZIP archives, DOCX, XLSX, etc.
     * • Any file type (Telegram doesn't restrict extensions)
     * • Files up to 50 MB (20 MB for free bots)
     *
     * For media files (photos, videos, audio), prefer specialized methods
     * ([sendPhoto], [sendVideo], etc.) for better in-chat rendering.
     *
     * @param chatId Unique identifier for the target chat or username (@channel)
     * @param document File to send ([InputFile] abstraction)
     * @param businessConnectionId Unique identifier of the business connection
     * @param messageThreadId Identifier of the message thread (for supergroups with topics)
     * @param directMessagesTopicId Topic ID for business DMs
     * @param thumbnail Document thumbnail (JPEG, max 200KB, 320x320) — shown for PDFs/images
     * @param caption Caption for the document (0-1024 characters)
     * @param parseMode Formatting mode for caption: [ParseMode.HTML], [ParseMode.MARKDOWN_V2], or null
     * @param captionEntities List of special entities for caption. If specified, [parseMode] is ignored
     * @param disableContentTypeDetection Disable automatic MIME type detection (send as generic file)
     * @param disableNotification Send silently (no notification to users)
     * @param protectContent Prevent forwarding/saving/downloading of the document
     * @param allowPaidBroadcast Allow sending to paid broadcast channels
     * @param messageEffectId Message effect to apply (Premium feature)
     * @param suggestedPostParameters Scheduling options for channel posts
     * @param replyParameters Reply configuration (quote, etc.)
     * @param replyMarkup Inline/reply keyboard attached to the message
     * @return The sent [Message] containing the document
     *
     * @throws TelegramApiException if:
     *   - chat not found or bot has no send rights
     *   - file is invalid or corrupted
     *   - file size exceeds limits (50 MB / 20 MB)
     *   - thumbnail is invalid format/size
     *
     * @see sendPhoto
     * @see sendVideo
     * @see sendAudio
     * @see InputFile
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
     * Send a video file (MP4) to a chat.
     *
     * Samples:
     * ```kotlin
     * // Send MP4 with custom thumbnail and caption
     * client.sendVideo(
     *     chatId = chatId,
     *     video = InputFile.fromPath("/videos/demo.mp4"),
     *     thumbnail = InputFile.fromUrl("https://example.com/thumb.jpg"),
     *     caption = "🎬 Check this out!",
     *     parseMode = ParseMode.HTML,
     *     duration = 45.seconds,
     *     width = 1280,
     *     height = 720,
     *     supportsStreaming = true
     * )
     *
     * // Send with spoiler (hidden until tapped)
     * client.sendVideo(
     *     chatId = chatId,
     *     video = InputFile.fromUrl("https://example.com/surprise.mp4"),
     *     caption = "🎁 Tap to reveal!",
     *     hasSpoiler = true
     * )
     *
     * // Start playback from specific timestamp
     * client.sendVideo(
     *     chatId = chatId,
     *     video = InputFile.fromId("BAAD..."), // existing video
     *     startTimestamp = 2.minutes,
     *     caption = "⏭️ Jump to the good part"
     * )
     * ```
     *
     * Videos are displayed with:
     * • In-chat player with play/pause/seek/volume
     * • Thumbnail preview (auto-generated or custom)
     * • Duration, resolution, and file size labels
     * • Optional caption above or below the video
     *
     * Video requirements:
     * • Format: MP4 (H.264 video + AAC audio recommended)
     * • Max size: 50 MB (20 MB for free bots)
     * • Max duration: No hard limit, but long videos may be rejected
     * • Aspect ratio: Any, but 16:9 or 9:16 recommended for best display
     *
     * @param chatId Unique identifier for the target chat or username (@channel)
     * @param video Video file to send ([InputFile] abstraction)
     * @param businessConnectionId Unique identifier of the business connection
     * @param messageThreadId Identifier of the message thread (for supergroups with topics)
     * @param directMessagesTopicId Topic ID for business DMs
     * @param duration Video duration (optional, but recommended for proper display)
     * @param width Video width in pixels (optional, helps with thumbnail generation)
     * @param height Video height in pixels (optional)
     * @param thumbnail Custom thumbnail (JPEG, max 200KB, recommended 320x320)
     * @param cover Cover image shown before playback (Premium feature)
     * @param startTimestamp Start playback from this timestamp.
     * @param caption Caption for the video (0-1024 characters)
     * @param parseMode Formatting mode for caption: [ParseMode.HTML], [ParseMode.MARKDOWN_V2], or null
     * @param captionEntities List of special entities for caption. If specified, [parseMode] is ignored
     * @param showCaptionAboveMedia Show caption above the video instead of below
     * @param hasSpoiler Hide the video behind a spoiler animation (user must tap to reveal)
     * @param supportsStreaming Mark video as streamable (enables progressive download)
     * @param disableNotification Send silently (no notification to users)
     * @param protectContent Prevent forwarding/saving/downloading of the video
     * @param allowPaidBroadcast Allow sending to paid broadcast channels
     * @param messageEffectId Message effect to apply (Premium feature)
     * @param suggestedPostParameters Scheduling options for channel posts
     * @param replyParameters Reply configuration (quote, etc.)
     * @param replyMarkup Inline/reply keyboard attached to the message
     * @return The sent [Message] containing the video
     *
     * @throws TelegramApiException if:
     *   - chat not found or bot has no send rights
     *   - video file is invalid, corrupted, or unsupported codec
     *   - thumbnail/cover is invalid format/size
     *   - file size exceeds limits
     *
     * @see sendPhoto
     * @see sendAnimation
     * @see sendVideoNote
     * @see editMessageMedia
     * @see InputFile
     */
    suspend fun sendVideo(
        chatId: ChatId,
        video: InputFile,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        duration: Duration? = null,
        width: Int? = null,
        height: Int? = null,
        thumbnail: InputFile? = null,
        cover: InputFile? = null,
        startTimestamp: Duration? = null,
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
     * Send a rounded square video message ("video note") to a chat.
     *
     * Samples:
     * ```kotlin
     * // Send a pre-recorded video note
     * client.sendVideoNote(
     *     chatId = chatId,
     *     videoNote = InputFile.fromPath("/notes/greeting.mp4"),
     *     duration = 15.seconds,
     *     length = 640, // full HD square
     *     thumbnail = InputFile.fromUrl("https://example.com/preview.jpg")
     * )
     *
     * // Send generated video note (e.g., screen recording)
     * val bytes = recordScreenClip() // your logic, must be square MP4
     * client.sendVideoNote(
     *     chatId = chatId,
     *     videoNote = InputFile.fromBytes(bytes, "clip.mp4"),
     *     duration = 30.seconds,
     *     length = 480
     * )
     * ```
     *
     * Video notes are displayed as:
     * • Circular player (like Telegram's native video messages)
     * • Waveform audio visualization
     * • Duration label
     * • No caption support (by Telegram API design)
     *
     * Video note requirements:
     * • Format: MP4 (H.264 video + AAC audio)
     * • Aspect ratio: 1:1 (square) — non-square videos are cropped
     * • Max duration: 60 seconds
     * • Max size: 20 MB
     * • Resolution: Recommended 640x640 (range: 1-640 for [length])
     *
     * @param chatId Unique identifier for the target chat or username (@channel)
     * @param videoNote Video note to send ([InputFile] abstraction, must be square MP4)
     * @param businessConnectionId Unique identifier of the business connection
     * @param messageThreadId Identifier of the message thread (for supergroups with topics)
     * @param directMessagesTopicId Topic ID for business DMs
     * @param duration Video note duration. (max 60 seconds, optional but recommended)
     * @param length Width and height of the video (diameter of the circular player, 1-640)
     * @param thumbnail Thumbnail for the video note (JPEG, max 200KB, recommended 320x320)
     * @param disableNotification Send silently (no notification to users)
     * @param protectContent Prevent forwarding/saving of the video note
     * @param allowPaidBroadcast Allow sending to paid broadcast channels
     * @param messageEffectId Message effect to apply (Premium feature)
     * @param suggestedPostParameters Scheduling options for channel posts
     * @param replyParameters Reply configuration (quote, etc.)
     * @param replyMarkup Inline/reply keyboard attached to the message
     * @return The sent [Message] containing the video note
     *
     * @throws TelegramApiException if:
     *   - chat not found or bot has no send rights
     *   - video is not square or exceeds 60 seconds
     *   - file size exceeds 20 MB
     *   - thumbnail is invalid format/size
     *
     * @see sendVideo
     * @see sendVoice
     * @see InputFile
     */
    suspend fun sendVideoNote(
        chatId: ChatId,
        videoNote: InputFile,
        businessConnectionId: String? = null,
        messageThreadId: Int? = null,
        directMessagesTopicId: Int? = null,
        duration: Duration? = null,
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
     * Send an animation (GIF or H.264/MPEG-4 video without sound) to a chat.
     *
     * Samples:
     * ```kotlin
     * // Send GIF from URL
     * client.sendAnimation(
     *     chatId = chatId,
     *     animation = InputFile.fromUrl("https://example.com/reaction.gif"),
     *     caption = "😂 When the code works on first try",
     *     parseMode = ParseMode.HTML
     * )
     *
     * // Send MP4 animation (smaller file size than GIF)
     * client.sendAnimation(
     *     chatId = chatId,
     *     animation = InputFile.fromPath("/animations/loading.mp4"),
     *     caption = "⏳ Loading...",
     *     width = 200,
     *     height = 200,
     *     duration = 3.seconds
     * )
     *
     * // Send with spoiler (hidden until tapped)
     * client.sendAnimation(
     *     chatId = chatId,
     *     animation = InputFile.fromId("CgAC..."), // existing animation
     *     caption = "🎁 Surprise!",
     *     hasSpoiler = true
     * )
     * ```
     *
     * Animations are displayed as:
     * • Autoplaying looped video (like GIFs)
     * • With play/pause controls on tap
     * • Optional caption above or below
     * • No audio track (sound is stripped by Telegram)
     *
     * Animation requirements:
     * • Format: GIF or MP4 (H.264 video, no audio)
     * • Max size: 50 MB (20 MB for free bots)
     * • Max duration: No hard limit, but short clips (<30s) recommended
     * • Resolution: Any, but keep file size reasonable for fast loading
     *
     * @param chatId Unique identifier for the target chat or username (@channel)
     * @param animation Animation file to send ([InputFile] abstraction)
     * @param businessConnectionId Unique identifier of the business connection
     * @param messageThreadId Identifier of the message thread (for supergroups with topics)
     * @param directMessagesTopicId Topic ID for business DMs
     * @param duration Animation duration. (optional, helps with proper display)
     * @param width Animation width in pixels (optional)
     * @param height Animation height in pixels (optional)
     * @param thumbnail Custom thumbnail (JPEG, max 200KB, recommended 320x320)
     * @param caption Caption for the animation (0-1024 characters)
     * @param parseMode Formatting mode for caption: [ParseMode.HTML], [ParseMode.MARKDOWN_V2], or null
     * @param captionEntities List of special entities for caption. If specified, [parseMode] is ignored
     * @param showCaptionAboveMedia Show caption above the animation instead of below
     * @param hasSpoiler Hide the animation behind a spoiler animation (user must tap to reveal)
     * @param disableNotification Send silently (no notification to users)
     * @param protectContent Prevent forwarding/saving of the animation
     * @param allowPaidBroadcast Allow sending to paid broadcast channels
     * @param messageEffectId Message effect to apply (Premium feature)
     * @param suggestedPostParameters Scheduling options for channel posts
     * @param replyParameters Reply configuration (quote, etc.)
     * @param replyMarkup Inline/reply keyboard attached to the message
     * @return The sent [Message] containing the animation
     *
     * @throws TelegramApiException if:
     *   - chat not found or bot has no send rights
     *   - animation file is invalid or corrupted
     *   - file has audio track (will be stripped, but may cause issues)
     *   - file size exceeds limits
     *
     * @see sendVideo
     * @see sendPhoto
     * @see sendDocument
     * @see InputFile
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