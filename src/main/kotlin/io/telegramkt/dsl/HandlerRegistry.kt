package io.telegramkt.dsl

import io.telegramkt.model.animation.Animation
import io.telegramkt.model.audio.Audio
import io.telegramkt.model.audio.Voice
import io.telegramkt.model.checklist.Checklist
import io.telegramkt.model.contact.Contact
import io.telegramkt.model.document.Document
import io.telegramkt.model.game.Dice
import io.telegramkt.model.location.Location
import io.telegramkt.model.location.Venue
import io.telegramkt.model.photo.PhotoSize
import io.telegramkt.model.photo.largest
import io.telegramkt.model.poll.Poll
import io.telegramkt.model.video.Video
import io.telegramkt.model.video.VideoNote

typealias Handler = suspend BotContext.() -> Unit
typealias ErrorHandler = suspend (Throwable, BotContext?) -> Unit

typealias CommandHandler = suspend BotContext.(args: List<String>) -> Unit
typealias PhotoHandler = suspend BotContext.(photo: PhotoSize, caption: String?) -> Unit
typealias MessageHandler = suspend BotContext.(text: String) -> Unit
typealias CallbackHandler = suspend BotContext.(data: String) -> Unit

typealias LocationHandler = suspend BotContext.(location: Location) -> Unit
typealias VenueHandler = suspend BotContext.(venue: Venue) -> Unit

typealias VideoHandler = suspend BotContext.(video: Video, caption: String?) -> Unit
typealias VideoNoteHandler = suspend BotContext.(videoNote: VideoNote) -> Unit
typealias AnimationHandler = suspend BotContext.(animation: Animation) -> Unit

typealias DocumentHandler = suspend BotContext.(document: Document, caption: String?) -> Unit

typealias AudioHandler = suspend BotContext.(audio: Audio, caption: String?) -> Unit
typealias VoiceHandler = suspend BotContext.(voice: Voice) -> Unit

typealias ContactHandler = suspend BotContext.(contact: Contact) -> Unit
typealias PollHandler = suspend BotContext.(poll: Poll) -> Unit

typealias ChecklistHandler = suspend BotContext.(checklist: Checklist) -> Unit
typealias DiceHandler = suspend BotContext.(dice: Dice) -> Unit

class HandlerRegistry {
    private val commands = mutableMapOf<String, CommandHandler>()
    private val callbacks = mutableMapOf<String, CallbackHandler>()
    private var onMessage: MessageHandler? = null
    private var onError: ErrorHandler = { ex, _ -> println("Error: ${ex.message}") }
    private val onPhoto = mutableListOf<PhotoHandler>()
    private val onLocation = mutableListOf<LocationHandler>()
    private val onVenue = mutableListOf<VenueHandler>()

    private val onVideo = mutableListOf<VideoHandler>()
    private val onVideoNote = mutableListOf<VideoNoteHandler>()
    private val onAnimation = mutableListOf<AnimationHandler>()
    private val onDocument = mutableListOf<DocumentHandler>()
    private val onAudio = mutableListOf<AudioHandler>()
    private val onVoice = mutableListOf<VoiceHandler>()
    private val onContact = mutableListOf<ContactHandler>()
    private val onPoll = mutableListOf<PollHandler>()
    private val onChecklist = mutableListOf<ChecklistHandler>()
    private val onDice = mutableListOf<DiceHandler>()

    fun onCommand(command: String, handler: CommandHandler) {
        commands[command.lowercase()] = handler
    }

    fun onCallback(dataPrefix: String, handler: CallbackHandler) {
        callbacks[dataPrefix] = handler
    }

    fun onMessage(handler: MessageHandler) {
        onMessage = handler
    }

    fun onError(handler: ErrorHandler) {
        onError = handler
    }

    fun onPhoto(handler: PhotoHandler) {
        onPhoto.add(handler)
    }

    fun onLocation(handler: LocationHandler) {
        onLocation.add(handler)
    }

    fun onVenue(handler: VenueHandler) {
        onVenue.add(handler)
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

    fun onContact(handler: ContactHandler) {
        onContact.add(handler)
    }

    fun onPoll(handler: PollHandler) {
        onPoll.add(handler)
    }

    fun onChecklist(handler: ChecklistHandler) {
        onChecklist.add(handler)
    }

    fun onDice(handler: DiceHandler) {
        onDice.add(handler)
    }

    internal suspend fun handle(ctx: BotContext) {
        try {
            when {
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

                ctx.callbackQuery?.data != null -> {
                    val data = ctx.callbackQuery?.data!!
                    callbacks.entries.find { data.startsWith(it.key) }
                        ?.value?.invoke(ctx, data)
                }

                ctx.message?.photo != null -> {
                    val photo = ctx.message?.photo?.largest()
                    val caption = ctx.message?.caption
                    if (photo != null) {
                        onPhoto.forEach { it(ctx, photo, caption) }
                    }
                }

                ctx.message?.text != null -> {
                    onMessage?.invoke(ctx, ctx.message?.text!!)
                }

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

                ctx.message?.contact != null -> {
                    val contact = ctx.message?.contact!!
                    onContact.forEach { it(ctx, contact) }
                }

                ctx.message?.checklist != null -> {
                    val checklist = ctx.message?.checklist!!
                    onChecklist.forEach { it(ctx, checklist) }
                }

                ctx.message?.dice != null -> {
                    val dice = ctx.message?.dice!!
                    onDice.forEach { it(ctx, dice) }
                }
            }
        } catch (ex: Exception) {
            onError(ex, ctx)
        }
    }
}