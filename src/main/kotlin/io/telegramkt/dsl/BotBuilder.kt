package io.telegramkt.dsl

import io.telegramkt.client.TelegramBotClient

class BotBuilder(private val token: String) {
    private val registry = HandlerRegistry()
    private var client: TelegramBotClient? = null

    fun onCommand(command: String, handler: CommandHandler) {
        registry.onCommand(command, handler)
    }

    fun onCallback(dataPrefix: String, handler: CallbackHandler) {
        registry.onCallback(dataPrefix, handler)
    }

    fun onReaction(handler: ReactionHandler) {
        registry.onReaction(handler)
    }

    fun onReactionCount(handler: ReactionCountHandler) {
        registry.onReactionCount(handler)
    }

    fun onMessage(handler: MessageHandler) {
        registry.onMessage(handler)
    }

    fun onEditedMessage(handler: EditedMessageHandler) {
        registry.onEditedMessage(handler)
    }

    fun onError(handler: ErrorHandler) {
        registry.onError(handler)
    }

    fun onLocation(handler: LocationHandler) {
        registry.onLocation(handler)
    }

    fun onVenue(handler: VenueHandler) {
        registry.onVenue(handler)
    }

    fun onPhoto(handler: PhotoHandler) {
        registry.onPhoto(handler)
    }

    fun onVideo(handler: VideoHandler) {
        registry.onVideo(handler)
    }

    fun onVideoNote(handler: VideoNoteHandler) {
        registry.onVideoNote(handler)
    }

    fun onAnimation(handler: AnimationHandler) {
        registry.onAnimation(handler)
    }

    fun onDocument(handler: DocumentHandler) {
        registry.onDocument(handler)
    }

    fun onAudio(handler: AudioHandler) {
        registry.onAudio(handler)
    }

    fun onVoice(handler: VoiceHandler) {
        registry.onVoice(handler)
    }

    fun onSticker(handler: StickerHandler) {
        registry.onSticker(handler)
    }

    fun onContact(handler: ContactHandler) {
        registry.onContact(handler)
    }

    fun onPoll(handler: PollHandler) {
        registry.onPoll(handler)
    }

    fun onPollAnswer(handler: PollAnswerHandler) {
        registry.onPollAnswer(handler)
    }

    fun onChecklist(handler: ChecklistHandler) {
        registry.onChecklist(handler)
    }

    fun onDice(handler: DiceHandler) {
        registry.onDice(handler)
    }

    fun onChannelPost(handler: ChannelPostHandler) {
        registry.onChannelPost(handler)
    }

    fun onEditedChannelPost(handler: EditedChannelPostHandler) {
        registry.onEditedChannelPost(handler)
    }

    fun onBusinessConnection(handler: BusinessConnectionHandler) {
        registry.onBusinessConnection(handler)
    }

    fun onBusinessMessage(handler: BusinessMessageHandler) {
        registry.onBusinessMessage(handler)
    }

    fun onEditedBusinessMessage(handler: EditedBusinessMessageHandler) {
        registry.onEditedBusinessMessage(handler)
    }

    fun onDeletedBusinessMessages(handler: DeletedBusinessMessagesHandler) {
        registry.onDeletedBusinessMessages(handler)
    }

    fun onInlineQuery(handler: InlineQueryHandler) {
        registry.onInlineQuery(handler)
    }

    fun onChosenInlineResult(handler: ChosenInlineResultHandler) {
        registry.onChosenInlineResult(handler)
    }

    fun onShippingQuery(handler: ShippingQueryHandler) {
        registry.onShippingQuery(handler)
    }

    fun onPreCheckoutQuery(handler: PreCheckoutQueryHandler) {
        registry.onPreCheckoutQuery(handler)
    }

    fun onPurchasedPaidMedia(handler: PurchasedPaidMediaHandler) {
        registry.onPurchasedPaidMedia(handler)
    }

    fun onMyChatMember(handler: MyChatMemberHandler) {
        registry.onMyChatMember(handler)
    }

    fun onChatMember(handler: ChatMemberHandler) {
        registry.onChatMember(handler)
    }

    fun onChatJoinRequest(handler: ChatJoinRequestHandler) {
        registry.onChatJoinRequest(handler)
    }

    fun onChatBoost(handler: ChatBoostHandler) {
        registry.onChatBoost(handler)
    }

    fun onRemovedChatBoost(handler: RemovedChatBoostHandler) {
        registry.onRemovedChatBoost(handler)
    }

    fun onManagedBot(handler: ManagedBotHandler) {
        registry.onManagedBot(handler)
    }

    fun client(configure: TelegramBotClient.() -> Unit) {
        client = TelegramBotClient(token).apply(configure)
    }

    fun build(): TelegramBot {
        val botClient = client ?: TelegramBotClient(token)
        return TelegramBot(botClient, registry)
    }
}

fun telegramBot(token: String, configure: BotBuilder.() -> Unit): TelegramBot {
    return BotBuilder(token).apply(configure).build()
}