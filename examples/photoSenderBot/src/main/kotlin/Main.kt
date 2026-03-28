package photoSenderBot

import io.telegramkt.dsl.telegramBot
import io.telegramkt.model.file.input.InputFile

/**
 * Photo Sender Bot - sends cat/dog photos on /getPhoto command.
 *
 * Usage: /getPhoto <cat|dog>
 *
 * Run:
 * BOT_TOKEN=your_token ./gradlew :examples:photoSenderBot:run
 */

fun main() {
    // Retrieve bot token from environment variables.
    // Never hardcode tokens in source code!
    val botToken = System.getenv("BOT_TOKEN") ?:
        throw  IllegalStateException("BOT_TOKEN environment variable is not set. Run: export BOT_TOKEN=your_token_here")

    // Initialize bot with command handlers.
    val bot = telegramBot(botToken) {
        /**
         * Handle /getPhoto command with argument.
         *
         * Examples:
         *   /getPhoto cat  -> sends cat photo
         *   /getPhoto dog  -> sends dog photo
         *   /getPhoto      -> error: missing argument
         */
        onCommand("getPhoto") { args ->
            // Check if photo code provided (cat/dog).
            args.firstOrNull()?.let { photoCode ->

                // Select photo based on code (case-insensitive).
                when (photoCode.lowercase()) {
                    "cat" -> {
                        // Send cat photo from TheCatAPI.
                        client.sendPhoto(
                            chatId = chatId!!,
                            photo = InputFile.fromUrl("https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg".trim())
                        )
                    }
                    "dog" -> {
                        // Send dog photo from Dog CEO API.
                        client.sendPhoto(
                            chatId = chatId!!,
                            photo = InputFile.fromUrl("https://images.dog.ceo/breeds/retriever-golden/n02099601_100.jpg".trim())
                        )
                    }
                    else -> {
                        // Unknown photo code - show available options.
                        reply("Unknown type, available:\ncat\ndog")
                    }
                }
            } ?: reply("Please, send a photo code.")
        }

        /**
         * Global error handler.
         * Catches all unhandled exceptions in handlers.
         */
        onError { ex, ctx ->
            println("Error: ${ex.message}")
        }
    }.startPolling()

    // Keep JVM alive.
    println("Photo sender started...")
    readln()
}