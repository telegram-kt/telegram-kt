package io.telegramkt.dsl

import io.telegramkt.model.animation.Animation
import io.telegramkt.model.audio.Audio
import io.telegramkt.model.audio.Voice
import io.telegramkt.model.bot.ManagedBotUpdated
import io.telegramkt.model.business.BusinessConnection
import io.telegramkt.model.business.BusinessMessagesDeleted
import io.telegramkt.model.chat.boost.ChatBoostRemoved
import io.telegramkt.model.chat.boost.ChatBoostUpdated
import io.telegramkt.model.chat.join.ChatJoinRequest
import io.telegramkt.model.chat.member.ChatMemberUpdated
import io.telegramkt.model.checklist.Checklist
import io.telegramkt.model.contact.Contact
import io.telegramkt.model.document.Document
import io.telegramkt.model.game.Dice
import io.telegramkt.model.inline.ChosenInlineResult
import io.telegramkt.model.inline.InlineQuery
import io.telegramkt.model.location.Location
import io.telegramkt.model.location.Venue
import io.telegramkt.model.message.Message
import io.telegramkt.model.payment.PaidMediaPurchased
import io.telegramkt.model.payment.order.PreCheckoutQuery
import io.telegramkt.model.payment.order.shipping.ShippingQuery
import io.telegramkt.model.photo.PhotoSize
import io.telegramkt.model.photo.largest
import io.telegramkt.model.poll.Poll
import io.telegramkt.model.poll.PollAnswer
import io.telegramkt.model.reaction.MessageReactionCountUpdated
import io.telegramkt.model.reaction.MessageReactionUpdated
import io.telegramkt.model.video.Video
import io.telegramkt.model.video.VideoNote


// ----------- Typealiases. -----------

typealias Handler = suspend BotContext.() -> Unit
// === Errors. ===
typealias ErrorHandler = suspend (Throwable, BotContext?) -> Unit

// === Commands. ===
typealias CommandHandler = suspend BotContext.(args: List<String>) -> Unit

// === Messages. ===
typealias MessageHandler = suspend BotContext.(text: String) -> Unit
typealias EditedMessageHandler = suspend BotContext.(message: Message) -> Unit

// === Callbacks. ===
typealias CallbackHandler = suspend BotContext.(data: String) -> Unit

// === Reactions. ===
typealias ReactionHandler = suspend BotContext.(reaction: MessageReactionUpdated) -> Unit
typealias ReactionCountHandler = suspend BotContext.(reactionCount: MessageReactionCountUpdated) -> Unit

// === Locations. ===
typealias LocationHandler = suspend BotContext.(location: Location) -> Unit
typealias VenueHandler = suspend BotContext.(venue: Venue) -> Unit

// === Files. ===
typealias PhotoHandler = suspend BotContext.(photo: PhotoSize, caption: String?) -> Unit
typealias VideoHandler = suspend BotContext.(video: Video, caption: String?) -> Unit
typealias VideoNoteHandler = suspend BotContext.(videoNote: VideoNote) -> Unit
typealias AnimationHandler = suspend BotContext.(animation: Animation) -> Unit

typealias DocumentHandler = suspend BotContext.(document: Document, caption: String?) -> Unit

typealias AudioHandler = suspend BotContext.(audio: Audio, caption: String?) -> Unit
typealias VoiceHandler = suspend BotContext.(voice: Voice) -> Unit

// === Contats. ===
typealias ContactHandler = suspend BotContext.(contact: Contact) -> Unit

// === Polls. ===
typealias PollHandler = suspend BotContext.(poll: Poll) -> Unit
typealias PollAnswerHandler = suspend BotContext.(pollAnswer: PollAnswer) -> Unit

// === Checklists. ===
typealias ChecklistHandler = suspend BotContext.(checklist: Checklist) -> Unit

// === Dices. ===
typealias DiceHandler = suspend BotContext.(dice: Dice) -> Unit

// === Channel Posts. ===
typealias ChannelPostHandler = suspend BotContext.(post: Message) -> Unit
typealias EditedChannelPostHandler = suspend BotContext.(post: Message) -> Unit

// === Business. ===
typealias BusinessConnectionHandler = suspend BotContext.(connection: BusinessConnection) -> Unit
typealias BusinessMessageHandler = suspend BotContext.(message: Message) -> Unit
typealias EditedBusinessMessageHandler = suspend BotContext.(message: Message) -> Unit
typealias DeletedBusinessMessagesHandler = suspend BotContext.(deleted: BusinessMessagesDeleted) -> Unit

// === Inline. ===
typealias InlineQueryHandler = suspend BotContext.(query: InlineQuery) -> Unit
typealias ChosenInlineResultHandler = suspend BotContext.(result: ChosenInlineResult) -> Unit

// === Payment. ===
typealias ShippingQueryHandler = suspend BotContext.(query: ShippingQuery) -> Unit
typealias PreCheckoutQueryHandler = suspend BotContext.(query: PreCheckoutQuery) -> Unit
typealias PurchasedPaidMediaHandler = suspend BotContext.(purchase: PaidMediaPurchased) -> Unit

// === Chat Member. ===
typealias MyChatMemberHandler = suspend BotContext.(updated: ChatMemberUpdated) -> Unit
typealias ChatMemberHandler = suspend BotContext.(updated: ChatMemberUpdated) -> Unit
typealias ChatJoinRequestHandler = suspend BotContext.(request: ChatJoinRequest) -> Unit

// === Chat Boost. ===
typealias ChatBoostHandler = suspend BotContext.(boost: ChatBoostUpdated) -> Unit
typealias RemovedChatBoostHandler = suspend BotContext.(boost: ChatBoostRemoved) -> Unit

// === Managed Bot. ===
typealias ManagedBotHandler = suspend BotContext.(updated: ManagedBotUpdated) -> Unit

class HandlerRegistry {
    // === Commands. ===
    private val commands = mutableMapOf<String, CommandHandler>()

    // === Callbacks. ===
    private val callbacks = mutableMapOf<String, CallbackHandler>()

    // === Errors. ===
    private var onError: ErrorHandler = { ex, _ -> println("Error: ${ex.message}") }

    // === Reactions. ===
    private val onReaction = mutableListOf<ReactionHandler>()
    private val onReactionCount = mutableListOf<ReactionCountHandler>()

    // === Messages. ===
    private var onMessage: MessageHandler? = null
    private val onEditedMessage = mutableListOf<EditedMessageHandler>()

    // === Locations. ===
    private val onLocation = mutableListOf<LocationHandler>()
    private val onVenue = mutableListOf<VenueHandler>()

    // === Files. ===
    private val onPhoto = mutableListOf<PhotoHandler>()
    private val onVideo = mutableListOf<VideoHandler>()
    private val onVideoNote = mutableListOf<VideoNoteHandler>()
    private val onAnimation = mutableListOf<AnimationHandler>()
    private val onDocument = mutableListOf<DocumentHandler>()
    private val onAudio = mutableListOf<AudioHandler>()
    private val onVoice = mutableListOf<VoiceHandler>()

    // === Contats. ===
    private val onContact = mutableListOf<ContactHandler>()

    // === Polls. ===
    private val onPoll = mutableListOf<PollHandler>()
    private val onPollAnswer = mutableListOf<PollAnswerHandler>()

    // === Checklists. ===
    private val onChecklist = mutableListOf<ChecklistHandler>()

    // === Dices. ===
    private val onDice = mutableListOf<DiceHandler>()

    // === Channel Posts. ===
    private val onChannelPost = mutableListOf<ChannelPostHandler>()
    private val onEditedChannelPost = mutableListOf<EditedChannelPostHandler>()

    // === Business. ===
    private val onBusinessConnection = mutableListOf<BusinessConnectionHandler>()
    private val onBusinessMessage = mutableListOf<BusinessMessageHandler>()
    private val onEditedBusinessMessage = mutableListOf<EditedBusinessMessageHandler>()
    private val onDeletedBusinessMessages = mutableListOf<DeletedBusinessMessagesHandler>()

    // === Inline. ===
    private val onInlineQuery = mutableListOf<InlineQueryHandler>()
    private val onChosenInlineResult = mutableListOf<ChosenInlineResultHandler>()

    // === Payment. ===
    private val onShippingQuery = mutableListOf<ShippingQueryHandler>()
    private val onPreCheckoutQuery = mutableListOf<PreCheckoutQueryHandler>()
    private val onPurchasedPaidMedia = mutableListOf<PurchasedPaidMediaHandler>()

    // === Chat Member. ===
    private val onMyChatMember = mutableListOf<MyChatMemberHandler>()
    private val onChatMember = mutableListOf<ChatMemberHandler>()
    private val onChatJoinRequest = mutableListOf<ChatJoinRequestHandler>()

    // === Chat Boost. ===
    private val onChatBoost = mutableListOf<ChatBoostHandler>()
    private val onRemovedChatBoost = mutableListOf<RemovedChatBoostHandler>()

    // === Managed Bot. ===
    private val onManagedBot = mutableListOf<ManagedBotHandler>()

    // ----------- Functions. -----------

    // === Commands. ===
    fun onCommand(command: String, handler: CommandHandler) {
        commands[command.lowercase()] = handler
    }

    // === Callbacks. ===
    fun onCallback(dataPrefix: String, handler: CallbackHandler) {
        callbacks[dataPrefix] = handler
    }

    // === Reactions. ===
    fun onReaction(handler: ReactionHandler) {
        onReaction.add(handler)
    }

    fun onReactionCount(handler: ReactionCountHandler) {
        onReactionCount.add(handler)
    }

    // === Messages. ===
    fun onMessage(handler: MessageHandler) {
        onMessage = handler
    }

    fun onEditedMessage(handler: EditedMessageHandler) {
        onEditedMessage.add(handler)
    }

    // === Errors. ===
    fun onError(handler: ErrorHandler) {
        onError = handler
    }

    // === Locations. ===
    fun onLocation(handler: LocationHandler) {
        onLocation.add(handler)
    }

    fun onVenue(handler: VenueHandler) {
        onVenue.add(handler)
    }

    // === Files. ===
    fun onPhoto(handler: PhotoHandler) {
        onPhoto.add(handler)
    }

    fun onVideoNote(handler: VideoNoteHandler) {
        onVideoNote.add(handler)
    }

    fun onAnimation(handler: AnimationHandler) {
        onAnimation.add(handler)
    }

    fun onDocument(handler: DocumentHandler) {
        onDocument.add(handler)
    }

    fun onAudio(handler: AudioHandler) {
        onAudio.add(handler)
    }

    fun onVideo(handler: VideoHandler) {
        onVideo.add(handler)
    }

    fun onVoice(handler: VoiceHandler) {
        onVoice.add(handler)
    }

    // === Contacts. ===
    fun onContact(handler: ContactHandler) {
        onContact.add(handler)
    }

    // === Polls. ===
    fun onPoll(handler: PollHandler) {
        onPoll.add(handler)
    }

    fun onPollAnswer(handler: PollAnswerHandler) {
        onPollAnswer.add(handler)
    }

    // === Checklists. ===
    fun onChecklist(handler: ChecklistHandler) {
        onChecklist.add(handler)
    }

    // === Dices. ===
    fun onDice(handler: DiceHandler) {
        onDice.add(handler)
    }

    // === Channel Posts. ===
    fun onChannelPost(handler: ChannelPostHandler) {
        onChannelPost.add(handler)
    }

    fun onEditedChannelPost(handler: EditedChannelPostHandler) {
        onEditedChannelPost.add(handler)
    }

    // === Business. ===
    fun onBusinessConnection(handler: BusinessConnectionHandler) {
        onBusinessConnection.add(handler)
    }

    fun onBusinessMessage(handler: BusinessMessageHandler) {
        onBusinessMessage.add(handler)
    }

    fun onEditedBusinessMessage(handler: EditedBusinessMessageHandler) {
        onEditedBusinessMessage.add(handler)
    }

    fun onDeletedBusinessMessages(handler: DeletedBusinessMessagesHandler) {
        onDeletedBusinessMessages.add(handler)
    }

    // === Inline. ===
    fun onInlineQuery(handler: InlineQueryHandler) {
        onInlineQuery.add(handler)
    }

    fun onChosenInlineResult(handler: ChosenInlineResultHandler) {
        onChosenInlineResult.add(handler)
    }

    // === Payment. ===
    fun onShippingQuery(handler: ShippingQueryHandler) {
        onShippingQuery.add(handler)
    }

    fun onPreCheckoutQuery(handler: PreCheckoutQueryHandler) {
        onPreCheckoutQuery.add(handler)
    }

    fun onPurchasedPaidMedia(handler: PurchasedPaidMediaHandler) {
        onPurchasedPaidMedia.add(handler)
    }

    // === Chat Member. ===
    fun onMyChatMember(handler: MyChatMemberHandler) {
        onMyChatMember.add(handler)
    }

    fun onChatMember(handler: ChatMemberHandler) {
        onChatMember.add(handler)
    }

    fun onChatJoinRequest(handler: ChatJoinRequestHandler) {
        onChatJoinRequest.add(handler)
    }

    // === Chat Boost. ===
    fun onChatBoost(handler: ChatBoostHandler) {
        onChatBoost.add(handler)
    }

    fun onRemovedChatBoost(handler: RemovedChatBoostHandler) {
        onRemovedChatBoost.add(handler)
    }

    // === Managed Bot. ===
    fun onManagedBot(handler: ManagedBotHandler) {
        onManagedBot.add(handler)
    }

    internal suspend fun handle(ctx: BotContext) {
        try {
            when {
                // === Commands. ===
                ctx.message?.text?.startsWith("/") == true -> {
                    val fullText = ctx.message?.text!!
                    val cmd = fullText
                        .removePrefix("/")
                        .split(' ')
                        .first()
                        .split('@')
                        .first()
                        .lowercase()
                    val args = fullText.split("\\s+".toRegex()).drop(1)

                    commands[cmd]?.invoke(ctx, args)
                }

                // === Callbacks. ===
                ctx.callbackQuery?.data != null -> {
                    val data = ctx.callbackQuery?.data!!
                    callbacks.entries.find { data.startsWith(it.key) }
                        ?.value?.invoke(ctx, data)
                }

                // === Reactions. ===
                ctx.update.messageReaction != null -> {
                    onReaction.forEach { it(ctx, ctx.update.messageReaction) }
                }

                ctx.update.messageReactionCount != null -> {
                    onReactionCount.forEach { it(ctx, ctx.update.messageReactionCount) }
                }

                // === Messages. ===
                ctx.message?.text != null -> {
                    onMessage?.invoke(ctx, ctx.message?.text!!)
                }

                ctx.update.editedMessage != null -> {
                    onEditedMessage.forEach { it(ctx, ctx.update.editedMessage!!) }
                }

                // === Locations. ===
                ctx.message?.location != null -> {
                    val location = ctx.message!!.location!!
                    onLocation.forEach { handler ->
                        handler.invoke(ctx, location)
                    }
                }

                ctx.message?.venue != null -> {
                    val venue = ctx.message!!.venue!!
                    onVenue.forEach { handler ->
                        handler.invoke(ctx, venue)
                    }
                }

                // === Files. ===
                ctx.message?.photo != null -> {
                    val photo = ctx.message?.photo?.largest()
                    val caption = ctx.message?.caption
                    if (photo != null) {
                        onPhoto.forEach { it(ctx, photo, caption) }
                    }
                }

                ctx.message?.video != null -> {
                    val video = ctx.message?.video!!
                    val caption = ctx.message?.caption
                    onVideo.forEach { it(ctx, video, caption) }
                }

                ctx.message?.videoNote != null -> {
                    val videoNote = ctx.message?.videoNote!!
                    onVideoNote.forEach { it(ctx, videoNote) }
                }

                ctx.message?.animation != null -> {
                    val animation = ctx.message?.animation!!
                    onAnimation.forEach { it(ctx, animation) }
                }

                ctx.message?.document != null -> {
                    val document = ctx.message?.document!!
                    val caption = ctx.message?.caption
                    onDocument.forEach { it(ctx, document, caption) }
                }

                ctx.message?.audio != null -> {
                    val audio = ctx.message?.audio!!
                    val caption = ctx.message?.caption
                    onAudio.forEach { it(ctx, audio, caption) }
                }

                ctx.message?.voice != null -> {
                    val voice = ctx.message?.voice!!
                    onVoice.forEach { it(ctx, voice) }
                }

                // === Contacts. ===
                ctx.message?.contact != null -> {
                    val contact = ctx.message?.contact!!
                    onContact.forEach { it(ctx, contact) }
                }

                // === Polls. ===
                ctx.update.poll != null -> {
                    onPoll.forEach { it(ctx, ctx.update.poll) }
                }

                ctx.update.pollAnswer != null -> {
                    onPollAnswer.forEach { it(ctx, ctx.update.pollAnswer) }
                }

                // === Checklists. ===
                ctx.message?.checklist != null -> {
                    val checklist = ctx.message?.checklist!!
                    onChecklist.forEach { it(ctx, checklist) }
                }

                // === Dices. ===
                ctx.message?.dice != null -> {
                    val dice = ctx.message?.dice!!
                    onDice.forEach { it(ctx, dice) }
                }

                // === Channel Posts. ===
                ctx.update.channelPost != null -> {
                    onChannelPost.forEach { it(ctx, ctx.update.channelPost!!) }
                }
                ctx.update.editedChannelPost != null -> {
                    onEditedChannelPost.forEach { it(ctx, ctx.update.editedChannelPost!!) }
                }

                // === Business. ===
                ctx.update.businessConnection != null -> {
                    onBusinessConnection.forEach { it(ctx, ctx.update.businessConnection!!) }
                }
                ctx.update.businessMessage != null -> {
                    onBusinessMessage.forEach { it(ctx, ctx.update.businessMessage!!) }
                }
                ctx.update.editedBusinessMessage != null -> {
                    onEditedBusinessMessage.forEach { it(ctx, ctx.update.editedBusinessMessage!!) }
                }
                ctx.update.deletedBusinessMessages != null -> {
                    onDeletedBusinessMessages.forEach { it(ctx, ctx.update.deletedBusinessMessages!!) }
                }

                // === Inline. ===
                ctx.update.inlineQuery != null -> {
                    onInlineQuery.forEach { it(ctx, ctx.update.inlineQuery!!) }
                }
                ctx.update.chosenInlineResult != null -> {
                    onChosenInlineResult.forEach { it(ctx, ctx.update.chosenInlineResult!!) }
                }

                // === Payment. ===
                ctx.update.shippingQuery != null -> {
                    onShippingQuery.forEach { it(ctx, ctx.update.shippingQuery!!) }
                }
                ctx.update.preCheckoutQuery != null -> {
                    onPreCheckoutQuery.forEach { it(ctx, ctx.update.preCheckoutQuery!!) }
                }
                ctx.update.purchasedPaidMedia != null -> {
                    onPurchasedPaidMedia.forEach { it(ctx, ctx.update.purchasedPaidMedia!!) }
                }

                // === Chat Member. ===
                ctx.update.myChatMember != null -> {
                    onMyChatMember.forEach { it(ctx, ctx.update.myChatMember!!) }
                }
                ctx.update.chatMember != null -> {
                    onChatMember.forEach { it(ctx, ctx.update.chatMember!!) }
                }
                ctx.update.chatJoinRequest != null -> {
                    onChatJoinRequest.forEach { it(ctx, ctx.update.chatJoinRequest!!) }
                }

                // === Chat Boost. ===
                ctx.update.chatBoost != null -> {
                    onChatBoost.forEach { it(ctx, ctx.update.chatBoost!!) }
                }
                ctx.update.removedChatBoost != null -> {
                    onRemovedChatBoost.forEach { it(ctx, ctx.update.removedChatBoost!!) }
                }

                // === Managed Bot. ===
                ctx.update.managedBot != null -> {
                    onManagedBot.forEach { it(ctx, ctx.update.managedBot!!) }
                }
            }
        } catch (ex: Exception) {
            onError(ex, ctx)
        }
    }
}