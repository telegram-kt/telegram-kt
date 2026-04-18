package io.telegramkt.dsl

import io.telegramkt.client.TelegramBotClient

/**
 * DSL builder for configuring a [TelegramBot] instance.
 *
 * Do not instantiate directly — use [telegramBot] factory function.
 *
 * This builder provides a fluent, type-safe API for registering handlers:
 * • `onCommand("start") { ... }` — handle /start command
 * • `onPhoto { photo, caption -> ... }` — handle photo messages
 * • `onCallback("buy:") { data -> ... }` — handle inline keyboard callbacks
 * • And 20+ more handler types for every Telegram update
 *
 * @param token Bot token from @BotFather (keep secret!)
 *
 * @see telegramBot
 * @see TelegramBot
 * @see HandlerRegistry
 */
class BotBuilder(private val token: String) {
    private val registry = HandlerRegistry()
    private var client: TelegramBotClient? = null

    /**
     * Register a handler for a bot command (e.g., /start, /help).
     *
     * Commands are matched case-insensitively and support bot username suffix:
     * • `/start` → matches
     * • `/start@MyBot` → matches if bot username is MyBot
     * • `/start@OtherBot` → does not match
     *
     * @param command Command name without slash (e.g., "start" for /start)
     * @param handler Suspended function receiving [BotContext] and command arguments
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     // Simple command
     *     onCommand("start") { args ->
     *         reply("Welcome! Use /help for commands")
     *     }
     *
     *     // Command with arguments
     *     onCommand("echo") { args ->
     *         val text = args.joinToString(" ")
     *         reply("You said: $text")
     *     }
     *
     *     // Command with HTML formatting
     *     onCommand("info") {
     *         reply(
     *             "<b>Bot Info</b>\nVersion: 1.0",
     *             parseMode = ParseMode.HTML
     *         )
     *     }
     * }
     * ```
     *
     * @see onMessage
     * @see BotContext.reply
     */
    fun onCommand(command: String, handler: CommandHandler) {
        registry.onCommand(command, handler)
    }

    /**
     * Register a handler for inline keyboard callback queries.
     *
     * Callbacks are matched by prefix using `startsWith`, enabling pattern routing:
     * • `onCallback("buy:")` → matches "buy:123", "buy:456", etc.
     * • `onCallback("vote_")` → matches "vote_yes", "vote_no", etc.
     *
     * You **must** call [BotContext.answerCallback] or [TelegramBotClient.answerCallbackQuery]
     * within ~30 seconds, or users will see an infinite loading indicator.
     *
     * @param dataPrefix Prefix to match against [io.telegramkt.model.callback.CallbackQuery.data]
     * @param handler Suspended function receiving [BotContext] and full callback data string
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     // Pattern-based routing
     *     onCallback("buy:") { data ->
     *         val productId = data.substringAfter("buy:")
     *         processPurchase(productId)
     *         answerCallback(text = "✅ Added to cart")
     *     }
     *
     *     // Confirmation flow
     *     onCallback("confirm_") { data ->
     *         val action = data.substringAfter("confirm_")
     *         if (action == "yes") {
     *             executeAction()
     *             answerCallback(text = "Done!", showAlert = true)
     *         } else {
     *             answerCallback(text = "Cancelled")
     *         }
     *     }
     * }
     * ```
     *
     * @see BotContext.answerCallback
     * @see TelegramBotClient.answerCallbackQuery
     * @see io.telegramkt.model.keyboard.reply.InlineKeyboardMarkup
     */
    fun onCallback(dataPrefix: String, handler: CallbackHandler) {
        registry.onCallback(dataPrefix, handler)
    }

    /**
     * Register a handler for user reactions on messages.
     *
     * Fires when a user adds, removes, or changes a reaction (❤️, 👍, 🔥, etc.).
     * Provides [io.telegramkt.model.reaction.MessageReactionUpdated] with:
     * • `oldReaction` / `newReaction` — lists of reactions before/after
     * • `user` — who changed the reaction (null for anonymous)
     * • Helper methods: `addedReaction()`, `wasAdded(ReactionType)`, etc.
     *
     * @param handler Suspended function receiving [BotContext] and reaction update
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onReaction { reaction ->
     *         // Thank users who add fire reaction
     *         if (reaction.wasAdded(Reaction.fire)) {
     *             reply("🔥 Thanks for the fire!")
     *         }
     *
     *         // Log reaction changes
     *         println("User ${reaction.user?.id}: ${reaction.addedReactions()}")
     *     }
     * }
     * ```
     *
     * @see onReactionCount
     * @see io.telegramkt.model.reaction.MessageReactionUpdated
     * @see io.telegramkt.model.reaction.Reaction
     */
    fun onReaction(handler: ReactionHandler) {
        registry.onReaction(handler)
    }

    /**
     * Register a handler for user reactions on messages.
     *
     * Fires when a updated reaction count.
     * Provides [io.telegramkt.model.reaction.MessageReactionCountUpdated] with:
     * • `chat` — chat where the number of reactions has been updated
     * • `messageId` — message id where the number of reactions has been updated.
     * • `reactions` — Number of reactions for each message.
     *
     * @param handler Suspended function receiving [BotContext] and reaction count update
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onReactionCount { reactionsCount ->
     *         // Log reactions changes
     *         println("New reactions count: ${reactionsCount.sum { reactions -> reactions.totalCount) }}")
     *     }
     * }
     * ```
     *
     * @see onReaction
     * @see io.telegramkt.model.reaction.MessageReactionUpdated
     * @see io.telegramkt.model.reaction.ReactionCount
     */
    fun onReactionCount(handler: ReactionCountHandler) {
        registry.onReactionCount(handler)
    }

    /**
     * Register a handler for user messages.
     *
     * Fires when a user send message to the bot.
     * Provides [io.telegramkt.model.message.Message] with:
     * • `chat` — chat where user send the message.
     * • `id` — ID of the message sent by the user
     * • And more others...
     *
     * @param handler Suspended function receiving [BotContext] and getting user message.
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onMessage { messageText ->
     *         reply(messageText) // Echo.
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.message.Message
     * @see onEditedMessage
     */
    fun onMessage(handler: MessageHandler) {
        registry.onMessage(handler)
    }

    /**
     * Register a handler for user messages.
     *
     * Fires when a user update old message.
     * Provides [io.telegramkt.model.message.Message] with:
     * • `chat` — chat where user send the message.
     * • `id` — ID of the message sent by the user
     * • And more others...
     *
     * @param handler Suspended function receiving [BotContext] and getting updated user message.
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onEditedMessage { newMessage ->
     *         reply("You are edited message, new text: ${newMessage.text ?: "Empty"}") // Echo.
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.message.Message
     * @see onEditedMessage
     */
    fun onEditedMessage(handler: EditedMessageHandler) {
        registry.onEditedMessage(handler)
    }

    /**
     * Register a handler for uncaught exceptions in bot logic.
     *
     * Fires when any registered handler throws an exception.
     * Use this for logging, alerting, or sending error reports to users.
     *
     * @param handler Suspended function receiving [Throwable] and optional [BotContext]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onError { ex, ctx ->
     *         println("❌ Error: ${ex.message}")
     *         ctx?.reply("⚠️ Something went wrong. Try again later.")
     *     }
     * }
     * ```
     *
     * @see HandlerRegistry.onError
     */
    fun onError(handler: ErrorHandler) {
        registry.onError(handler)
    }

    /**
     * Register a handler for location messages.
     *
     * Fires when a user sends a geographic location.
     * Provides [io.telegramkt.model.location.Location] with latitude, longitude, accuracy, etc.
     *
     * @param handler Suspended function receiving [BotContext] and [io.telegramkt.model.location.Location]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onLocation { location ->
     *         reply("📍 You're at: ${location.latitude}, ${location.longitude}")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.location.Location
     * @see onVenue
     */
    fun onLocation(handler: LocationHandler) {
        registry.onLocation(handler)
    }

    /**
     * Register a handler for venue messages.
     *
     * Fires when a user sends a venue (place with name, address, location).
     * Provides [io.telegramkt.model.location.Venue] with title, address, foursquare/google IDs, etc.
     *
     * @param handler Suspended function receiving [BotContext] and [io.telegramkt.model.location.Venue]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onVenue { venue ->
     *         reply("🏢 You're at: ${venue.title}\n📍 ${venue.address}")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.location.Venue
     * @see onLocation
     */
    fun onVenue(handler: VenueHandler) {
        registry.onVenue(handler)
    }

    /**
     * Register a handler for photo messages.
     *
     * Fires when a user sends a photo. Provides the largest [io.telegramkt.model.photo.PhotoSize] variant
     * and optional caption. Use [io.telegramkt.model.photo.PhotoSize.width]/[io.telegramkt.model.photo.PhotoSize.height] for resolution info.
     *
     * @param handler Suspended function receiving [BotContext], [io.telegramkt.model.photo.PhotoSize], and caption
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onPhoto { photo, caption ->
     *         reply("📸 Nice shot! (${photo.width}x${photo.height})\n${caption ?: ""}")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.photo.PhotoSize
     * @see io.telegramkt.model.photo.largest
     * @see BotContext.replyPhoto
     */
    fun onPhoto(handler: PhotoHandler) {
        registry.onPhoto(handler)
    }

    /**
     * Register a handler for video messages.
     *
     * Fires when a user sends a video file. Provides [io.telegramkt.model.video.Video] metadata and optional caption.
     *
     * @param handler Suspended function receiving [BotContext], [io.telegramkt.model.video.Video], and caption
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onVideo { video, caption ->
     *         reply("🎬 Video received! Duration: ${video.duration}s\n${caption ?: ""}")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.video.Video
     * @see BotContext.replyVideo
     */
    fun onVideo(handler: VideoHandler) {
        registry.onVideo(handler)
    }

    /**
     * Register a handler for video note messages (rounded square videos).
     *
     * Fires when a user sends a video note. Provides [io.telegramkt.model.video.VideoNote] with duration and size info.
     *
     * @param handler Suspended function receiving [BotContext] and [io.telegramkt.model.video.VideoNote]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onVideoNote { videoNote ->
     *         reply("🔴 Video note: ${videoNote.duration}s")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.video.VideoNote
     * @see BotContext.replyVideoNote
     */
    fun onVideoNote(handler: VideoNoteHandler) {
        registry.onVideoNote(handler)
    }

    /**
     * Register a handler for animation messages (GIFs or silent MP4).
     *
     * Fires when a user sends an animation. Provides [io.telegramkt.model.animation.Animation] metadata.
     *
     * @param handler Suspended function receiving [BotContext] and [io.telegramkt.model.animation.Animation]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onAnimation { animation ->
     *         reply("🎞️ GIF received! ${animation.width}x${animation.height}")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.animation.Animation
     * @see BotContext.replyAnimation
     */
    fun onAnimation(handler: AnimationHandler) {
        registry.onAnimation(handler)
    }

    /**
     * Register a handler for document messages (general files).
     *
     * Fires when a user sends a file (PDF, ZIP, DOCX, etc.). Provides [io.telegramkt.model.document.Document] with name, MIME type, size.
     *
     * @param handler Suspended function receiving [BotContext], [io.telegramkt.model.document.Document], and caption
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onDocument { document, caption ->
     *         reply("📄 File: ${document.fileName}\n📦 ${document.fileSize} bytes\n${caption ?: ""}")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.document.Document
     * @see BotContext.replyDocument
     */
    fun onDocument(handler: DocumentHandler) {
        registry.onDocument(handler)
    }

    /**
     * Register a handler for audio messages (music tracks).
     *
     * Fires when a user sends an audio file. Provides [io.telegramkt.model.audio.Audio] with title, performer, duration.
     *
     * @param handler Suspended function receiving [BotContext], [io.telegramkt.model.audio.Audio], and caption
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onAudio { audio, caption ->
     *         reply("🎵 ${audio.performer} — ${audio.title}\n⏱️ ${audio.duration}s\n${caption ?: ""}")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.audio.Audio
     * @see BotContext.replyAudio
     */
    fun onAudio(handler: AudioHandler) {
        registry.onAudio(handler)
    }

    /**
     * Register a handler for voice messages (recorded speech).
     *
     * Fires when a user sends a voice message (OGG_OPUS). Provides [io.telegramkt.model.audio.Voice] with duration.
     *
     * @param handler Suspended function receiving [BotContext] and [io.telegramkt.model.audio.Voice]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onVoice { voice ->
     *         reply("🎙️ Voice message: ${voice.duration}s")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.audio.Voice
     * @see BotContext.replyVoice
     */
    fun onVoice(handler: VoiceHandler) {
        registry.onVoice(handler)
    }

    /**
     * Register a handler for sticker messages.
     *
     * Fires when a user sends a sticker. Provides [io.telegramkt.model.sticker.Sticker] with emoji, set info, dimensions.
     *
     * @param handler Suspended function receiving [BotContext] and [io.telegramkt.model.sticker.Sticker]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onSticker { sticker ->
     *         reply("🎭 Sticker: ${sticker.emoji ?: "no emoji"}")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.sticker.Sticker
     */
    fun onSticker(handler: StickerHandler) {
        registry.onSticker(handler)
    }

    /**
     * Register a handler for contact messages.
     *
     * Fires when a user shares a contact. Provides [io.telegramkt.model.contact.Contact] with phone, name, vCard.
     *
     * @param handler Suspended function receiving [BotContext] and [io.telegramkt.model.contact.Contact]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onContact { contact ->
     *         reply("📞 ${contact.firstName} ${contact.lastName}\n${contact.phoneNumber}")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.contact.Contact
     */
    fun onContact(handler: ContactHandler) {
        registry.onContact(handler)
    }

    /**
     * Register a handler for poll updates.
     *
     * Fires when a poll is updated (new votes, closed, etc.). Provides [io.telegramkt.model.poll.Poll] with question, options, voter count.
     *
     * @param handler Suspended function receiving [BotContext] and [io.telegramkt.model.poll.Poll]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onPoll { poll ->
     *         reply("📊 Poll: ${poll.question}\n✅ Votes: ${poll.totalVoterCount}")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.poll.Poll
     * @see onPollAnswer
     */
    fun onPoll(handler: PollHandler) {
        registry.onPoll(handler)
    }

    /**
     * Register a handler for poll answers (user votes).
     *
     * Fires when a user votes in a poll. Provides [io.telegramkt.model.poll.PollAnswer] with user, poll ID, selected options.
     *
     * @param handler Suspended function receiving [BotContext] and [io.telegramkt.model.poll.PollAnswer]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onPollAnswer { answer ->
     *         println("🗳️ User ${answer.user?.id} voted: ${answer.optionIds}")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.poll.PollAnswer
     * @see onPoll
     */
    fun onPollAnswer(handler: PollAnswerHandler) {
        registry.onPollAnswer(handler)
    }

    /**
     * Register a handler for checklist messages.
     *
     * Fires when a user sends a checklist (Telegram Premium feature). Provides [io.telegramkt.model.checklist.Checklist] with items.
     *
     * @param handler Suspended function receiving [BotContext] and [io.telegramkt.model.checklist.Checklist]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onChecklist { checklist ->
     *         reply("✅ Checklist received: ${checklist.items.size} items")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.checklist.Checklist
     */
    fun onChecklist(handler: ChecklistHandler) {
        registry.onChecklist(handler)
    }

    /**
     * Register a handler for dice messages.
     *
     * Fires when a user sends a dice/emoji game. Provides [io.telegramkt.model.game.Dice] with emoji and value.
     *
     * @param handler Suspended function receiving [BotContext] and [io.telegramkt.model.game.Dice]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onDice { dice ->
     *         reply("🎲 You rolled: ${dice.value} (${dice.emoji})")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.game.Dice
     */
    fun onDice(handler: DiceHandler) {
        registry.onDice(handler)
    }

    /**
     * Register a handler for channel posts.
     *
     * Fires when a new post appears in a channel where the bot is admin.
     * Provides [io.telegramkt.model.message.Message] with post content.
     *
     * @param handler Suspended function receiving [BotContext] and channel post [io.telegramkt.model.message.Message]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onChannelPost { post ->
     *         println("📢 New channel post: ${post.text?.take(50)}")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.message.Message
     * @see onEditedChannelPost
     */
    fun onChannelPost(handler: ChannelPostHandler) {
        registry.onChannelPost(handler)
    }

    /**
     * Register a handler for edited channel posts.
     *
     * Fires when a channel post is edited. Provides updated [io.telegramkt.model.message.Message].
     *
     * @param handler Suspended function receiving [BotContext] and edited post [io.telegramkt.model.message.Message]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onEditedChannelPost { post ->
     *         println("✏️ Channel post edited: ${post.messageId}")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.message.Message
     * @see onChannelPost
     */
    fun onEditedChannelPost(handler: EditedChannelPostHandler) {
        registry.onEditedChannelPost(handler)
    }

    /**
     * Register a handler for business connection events.
     *
     * Fires when a business connection is established/updated. Provides [io.telegramkt.model.business.BusinessConnection] details.
     *
     * @param handler Suspended function receiving [BotContext] and [io.telegramkt.model.business.BusinessConnection]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onBusinessConnection { connection ->
     *         println("🔗 Business connected: ${connection.userId}")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.business.BusinessConnection
     */
    fun onBusinessConnection(handler: BusinessConnectionHandler) {
        registry.onBusinessConnection(handler)
    }

    /**
     * Register a handler for business messages.
     *
     * Fires when a message is received via a business connection. Provides [io.telegramkt.model.message.Message] with business context.
     *
     * @param handler Suspended function receiving [BotContext] and business [io.telegramkt.model.message.Message]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onBusinessMessage { message ->
     *         reply("💼 Business message: ${message.text}")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.message.Message
     */
    fun onBusinessMessage(handler: BusinessMessageHandler) {
        registry.onBusinessMessage(handler)
    }

    /**
     * Register a handler for edited business messages.
     *
     * Fires when a business message is edited. Provides updated [io.telegramkt.model.message.Message].
     *
     * @param handler Suspended function receiving [BotContext] and edited business [io.telegramkt.model.message.Message]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onEditedBusinessMessage { message ->
     *         println("✏️ Business message edited")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.message.Message
     */
    fun onEditedBusinessMessage(handler: EditedBusinessMessageHandler) {
        registry.onEditedBusinessMessage(handler)
    }

    /**
     * Register a handler for deleted business messages.
     *
     * Fires when messages are deleted in a business chat. Provides [io.telegramkt.model.business.BusinessMessagesDeleted] with chat and message IDs.
     *
     * @param handler Suspended function receiving [BotContext] and [io.telegramkt.model.business.BusinessMessagesDeleted]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onDeletedBusinessMessages { deleted ->
     *         println("🗑️ ${deleted.messageIds.size} business messages deleted")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.business.BusinessMessagesDeleted
     */
    fun onDeletedBusinessMessages(handler: DeletedBusinessMessagesHandler) {
        registry.onDeletedBusinessMessages(handler)
    }

    /**
     * Register a handler for inline queries.
     *
     * Fires when a user types @yourbot in any chat. Provides [io.telegramkt.model.inline.InlineQuery] with query text, user, location.
     *
     * You must call [TelegramBotClient.answerInlineQuery] to respond, or the user sees a loading indicator.
     *
     * @param handler Suspended function receiving [BotContext] and [io.telegramkt.model.inline.InlineQuery]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onInlineQuery { query ->
     *         val results = listOf(
     *             InlineQueryResult.Article(
     *                 id = "1",
     *                 title = "Result",
     *                 inputMessageContent = InputMessageContent.Text("You searched: ${query.query}")
     *             )
     *         )
     *         answerInlineQuery(results)
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.inline.InlineQuery
     * @see TelegramBotClient.answerInlineQuery
     */
    fun onInlineQuery(handler: InlineQueryHandler) {
        registry.onInlineQuery(handler)
    }

    /**
     * Register a handler for chosen inline results.
     *
     * Fires when a user selects a result from your inline query response.
     * Provides [io.telegramkt.model.inline.ChosenInlineResult] with result ID, query, location.
     *
     * @param handler Suspended function receiving [BotContext] and [io.telegramkt.model.inline.ChosenInlineResult]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onChosenInlineResult { result ->
     *         println("✅ User chose result: ${result.resultId} for query: ${result.query}")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.inline.ChosenInlineResult
     * @see onInlineQuery
     */
    fun onChosenInlineResult(handler: ChosenInlineResultHandler) {
        registry.onChosenInlineResult(handler)
    }

    fun onShippingQuery(handler: ShippingQueryHandler) {
        registry.onShippingQuery(handler)
    }

    fun onPreCheckoutQuery(handler: PreCheckoutQueryHandler) {
        registry.onPreCheckoutQuery(handler)
    }

    /**
     * Register a handler for purchased paid media.
     *
     * Fires when a user buys paid media (Premium feature). Provides [io.telegramkt.model.payment.PaidMediaPurchased] with payload.
     *
     * @param handler Suspended function receiving [BotContext] and [io.telegramkt.model.payment.PaidMediaPurchased]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onPurchasedPaidMedia { purchase ->
     *         println("💰 Paid media purchased: ${purchase.payload}")
     *         // Unlock content for user
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.payment.PaidMediaPurchased
     */
    fun onPurchasedPaidMedia(handler: PurchasedPaidMediaHandler) {
        registry.onPurchasedPaidMedia(handler)
    }

    /**
     * Register a handler for bot's own chat member updates.
     *
     * Fires when the bot's status changes in a chat (added, removed, promoted, etc.).
     * Provides [io.telegramkt.model.chat.member.ChatMemberUpdated] with old/new status.
     *
     * @param handler Suspended function receiving [BotContext] and [io.telegramkt.model.chat.member.ChatMemberUpdated]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onMyChatMember { update ->
     *         println("🤖 Bot status changed: ${update.oldChatMember.status} → ${update.newChatMember.status}")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.chat.member.ChatMemberUpdated
     * @see onChatMember
     */
    fun onMyChatMember(handler: MyChatMemberHandler) {
        registry.onMyChatMember(handler)
    }

    /**
     * Register a handler for other users' chat member updates.
     *
     * Fires when any user's status changes in a chat (if bot has rights to see it).
     * Provides [io.telegramkt.model.chat.member.ChatMemberUpdated] with user, old/new status.
     *
     * @param handler Suspended function receiving [BotContext] and [io.telegramkt.model.chat.member.ChatMemberUpdated]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onChatMember { update ->
     *         println("👤 ${update.from?.username} changed status: ${update.newChatMember.status}")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.chat.member.ChatMemberUpdated
     * @see onMyChatMember
     */
    fun onChatMember(handler: ChatMemberHandler) {
        registry.onChatMember(handler)
    }

    /**
     * Register a handler for chat join requests.
     *
     * Fires when a user requests to join a private channel/supergroup (if approval required).
     * Provides [io.telegramkt.model.chat.join.ChatJoinRequest] with user, invite link, bio. Respond with [TelegramBotClient.approveChatJoinRequest] or [TelegramBotClient.declineChatJoinRequest].
     *
     * @param handler Suspended function receiving [BotContext] and [io.telegramkt.model.chat.join.ChatJoinRequest]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onChatJoinRequest { request ->
     *         println("🚪 Join request from ${request.from?.username}")
     *         // Auto-approve or review manually
     *         approveChatJoinRequest(request.chat.id, request.from!!.id)
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.chat.join.ChatJoinRequest
     * @see TelegramBotClient.approveChatJoinRequest
     * @see TelegramBotClient.declineChatJoinRequest
     */
    fun onChatJoinRequest(handler: ChatJoinRequestHandler) {
        registry.onChatJoinRequest(handler)
    }

    /**
     * Register a handler for chat boost events.
     *
     * Fires when a chat receives a new boost (Premium feature). Provides [io.telegramkt.model.chat.boost.ChatBoostUpdated] with boost details.
     *
     * @param handler Suspended function receiving [BotContext] and [io.telegramkt.model.chat.boost.ChatBoostUpdated]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onChatBoost { boost ->
     *         println("🚀 Chat boosted by ${boost.boost.source?.user?.username}")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.chat.boost.ChatBoostUpdated
     * @see onRemovedChatBoost
     */
    fun onChatBoost(handler: ChatBoostHandler) {
        registry.onChatBoost(handler)
    }

    /**
     * Register a handler for removed chat boosts.
     *
     * Fires when a boost is removed from a chat. Provides [io.telegramkt.model.chat.boost.ChatBoostRemoved] with reason.
     *
     * @param handler Suspended function receiving [BotContext] and [io.telegramkt.model.chat.boost.ChatBoostRemoved]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onRemovedChatBoost { removed ->
     *         println("📉 Boost removed: ${removed.reason}")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.chat.boost.ChatBoostRemoved
     * @see onChatBoost
     */
    fun onRemovedChatBoost(handler: RemovedChatBoostHandler) {
        registry.onRemovedChatBoost(handler)
    }

    /**
     * Register a handler for managed bot updates.
     *
     * Fires when a bot is managed via Telegram Business API. Provides [io.telegramkt.model.bot.ManagedBotUpdated] with connection info.
     *
     * @param handler Suspended function receiving [BotContext] and [io.telegramkt.model.bot.ManagedBotUpdated]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     onManagedBot { update ->
     *         println("🔧 Managed bot update: ${update.connectionId}")
     *     }
     * }
     * ```
     *
     * @see io.telegramkt.model.bot.ManagedBotUpdated
     */
    fun onManagedBot(handler: ManagedBotHandler) {
        registry.onManagedBot(handler)
    }

    /**
     * Configure the underlying [TelegramBotClient] with custom settings.
     *
     * Use this for advanced configuration:
     * • Custom HTTP client (timeouts, proxy, logging)
     * • Custom JSON serializer settings
     * • Request interceptors for metrics/auth
     *
     * If not called, a default client is created with sensible defaults.
     *
     * @param configure Lambda with receiver to customize [TelegramBotClient]
     *
     * Samples:
     * ```kotlin
     * telegramBot("TOKEN") {
     *     // Custom HTTP client with longer timeouts
     *     client {
     *         httpClient = HttpClient(CIO) {
     *             install(HttpTimeout) {
     *                 requestTimeoutMillis = 30_000
     *                 socketTimeoutMillis = 30_000
     *             }
     *         }
     *     }
     *
     *     onCommand("start") { reply("Hi!") }
     * }
     * ```
     *
     * @see TelegramBotClient
     * @see io.ktor.client.HttpClient
     */
    fun client(configure: TelegramBotClient.() -> Unit) {
        client = TelegramBotClient(token).apply(configure)
    }

    /**
     * Build and return the configured [TelegramBot] instance.
     *
     * Called automatically by [telegramBot] factory function.
     * Do not call manually unless building bot imperatively.
     *
     * @return Fully configured [TelegramBot] ready for [TelegramBot.startPolling]
     *
     * @see telegramBot
     * @see TelegramBot
     */
    fun build(): TelegramBot {
        val botClient = client ?: TelegramBotClient(token)
        return TelegramBot(botClient, registry)
    }
}

/**
 * Create and configure a new [TelegramBot] instance using DSL syntax.
 *
 * This is the **main entry point** for using the TelegramKt library.
 * Provides a fluent, type-safe API for registering handlers and starting the bot.
 *
 * @param token Bot token from @BotFather (https://t.me/BotFather)
 *              Keep this secret — never commit to version control!
 * @param configure Lambda with [BotBuilder] receiver to register handlers
 * @return Configured [TelegramBot] instance (call [TelegramBot.startPolling] to run)
 *
 * @throws IllegalArgumentException if token is empty or invalid format
 * @throws io.telegramkt.exception.TelegramApiException if initial API call fails (e.g., invalid token)
 *
 * Samples:
 * ```kotlin
 * // Minimal echo bot
 * fun main() = runBlocking {
 *     val bot = telegramBot(System.getenv("BOT_TOKEN")) {
 *         onCommand("start") { args ->
 *             reply("👋 Hi! I'm an echo bot.")
 *         }
 *
 *         onMessage { text ->
 *             reply("🔁 You said: $text")
 *         }
 *
 *         onError { ex, ctx ->
 *             println("Error: ${ex.message}")
 *             ctx?.reply("⚠️ Something went wrong")
 *         }
 *     }
 *
 *     bot.dropPendingUpdates()  // skip old messages
 *     bot.startPolling()        // start receiving updates
 *
 *     // Graceful shutdown
 *     Runtime.getRuntime().addShutdownHook(Thread { bot.stop() })
 * }
 *
 * // More advanced: media + callbacks
 * telegramBot("TOKEN") {
 *     onPhoto { photo, caption ->
 *         replyPhoto(
 *             photo = InputFile.fromId(photo.fileId),  // echo same photo
 *             caption = "📸 Nice shot!${caption?.let { "\n\n$caption" } ?: ""}"
 *         )
 *     }
 *
 *     onCallback("like:") { data ->
 *         val postId = data.substringAfter("like:")
 *         incrementLikes(postId)
 *         answerCallback(text = "👍 Liked!", cacheTime = 60)
 *     }
 * }
 * ```
 *
 * @see BotBuilder
 * @see TelegramBot
 * @see BotContext
 */
fun telegramBot(token: String, configure: BotBuilder.() -> Unit): TelegramBot {
    return BotBuilder(token).apply(configure).build()
}