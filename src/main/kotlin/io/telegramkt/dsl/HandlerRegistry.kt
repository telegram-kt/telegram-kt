package io.telegramkt.dsl

import io.telegramkt.model.photo.PhotoSize
import io.telegramkt.model.photo.largest

typealias Handler = suspend BotContext.() -> Unit
typealias ErrorHandler = suspend (Throwable, BotContext?) -> Unit

typealias CommandHandler = suspend BotContext.(args: List<String>) -> Unit
typealias PhotoHandler = suspend BotContext.(photo: PhotoSize, caption: String?) -> Unit
typealias MessageHandler = suspend BotContext.(text: String) -> Unit
typealias CallbackHandler = suspend BotContext.(data: String) -> Unit

class HandlerRegistry {
    private val commands = mutableMapOf<String, CommandHandler>()
    private val callbacks = mutableMapOf<String, CallbackHandler>()
    private var onMessage: MessageHandler? = null
    private var onError: ErrorHandler = { ex, _ -> println("Error: ${ex.message}") }
    private val onPhoto = mutableListOf<PhotoHandler>()

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

    internal suspend fun handle(ctx: BotContext) {
        try {
            when {
                ctx.message?.text?.startsWith("/") == true -> {
                    val fullText = ctx.message?.text!!
                    val cmd = fullText.substring(1).split('@').first().lowercase()
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
            }
        } catch (ex: Exception) {
            onError(ex, ctx)
        }
    }
}