package echoBot

import io.telegramkt.dsl.telegramBot

fun main() {
    // Retrieve bot token from environment variables.
    // Never hardcode tokens in source code. Seriously. Don't be that guy.
    val botToken = System.getenv("BOT_TOKEN") ?:
        throw  IllegalStateException("BOT_TOKEN environment variable is not set. Run: export BOT_TOKEN=your_token_here")

    // Initialize the bot instance with a simple echo handler.
    val bot = telegramBot(botToken) {
        // Echo back any text message received.
        // The 'reply' function is available through BotContext receiver.
        onMessage { text ->
            reply("Echo: $text")
        }
    }

    // Start polling for updates from Telegram servers.
    bot.startPolling()
    println("Echo bot started...")
    readln() // Keep the JVM alive. Without this, the program exits immediately.
}