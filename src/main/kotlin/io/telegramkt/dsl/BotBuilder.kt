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

    fun onMessage(handler: MessageHandler) {
        registry.onMessage(handler)
    }

    fun onError(handler: ErrorHandler) {
        registry.onError(handler)
    }

    fun onPhoto(handler: PhotoHandler) {
        registry.onPhoto(handler)
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