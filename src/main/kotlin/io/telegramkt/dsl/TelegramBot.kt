package io.telegramkt.dsl

import io.telegramkt.client.TelegramBotClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Main bot instance that manages polling, update handling, and lifecycle.
 *
 * Created via [telegramBot] DSL function. Do not instantiate directly.
 *
 * @param client Internal [TelegramBotClient] for API calls
 * @param registry [HandlerRegistry] containing all registered handlers
 *
 * @see telegramBot
 * @see BotBuilder
 * @see TelegramBotClient
 */
class TelegramBot(
    private val client: TelegramBotClient,
    private val registry: HandlerRegistry,
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var pollingJob: Job? = null

    /**
     * Start long-polling to receive updates from Telegram.
     *
     * This method launches a coroutine that continuously fetches updates
     * and dispatches them to registered handlers ([BotBuilder.onCommand], [BotBuilder.onMessage], etc.).
     *
     * This method is **non-blocking** — it returns immediately after starting the poll loop.
     * Use [stop] to gracefully shut down the bot.
     *
     * @param limit Maximum number of updates to fetch per request (1-100, default: 100)
     * @param timeout Timeout in seconds for long-polling (0-86400, default: 30).
     *                Higher values reduce request frequency but increase latency.
     * @param allowedUpdates List of update types to receive (e.g., `["message", "callback_query"]`).
     *                       If null, receives all update types except `["message", "channel_post", "edited_message"]`.
     * @return This [TelegramBot] instance for method chaining (fluent interface)
     *
     * @throws IllegalStateException if polling is already running
     * @throws io.telegramkt.exception.TelegramApiException if initial getUpdates call fails
     *
     * Samples:
     * ```kotlin
     * // Basic usage
     * val bot = telegramBot("TOKEN") {
     *     onCommand("start") { reply("Hi!") }
     * }
     * bot.startPolling()
     *
     * // With custom polling settings
     * bot.startPolling(
     *     limit = 50,           // fetch smaller batches
     *     timeout = 60,         // longer timeout for fewer requests
     *     allowedUpdates = listOf("message", "callback_query") // only care about these
     * )
     *
     * // Fluent chaining
     * telegramBot("TOKEN") { /* config */ }
     *     .dropPendingUpdates()
     *     .startPolling()
     * ```
     *
     * @see dropPendingUpdates
     * @see stop
     * @see TelegramBotClient.updatesFlow
     */
    fun startPolling(
        limit: Int = 100,
        timeout: Int = 30,
        allowedUpdates: List<String>? = null,
    ): TelegramBot {
        pollingJob = scope.launch {
            println("Bot started polling...")
            client.updatesFlow(limit, timeout, allowedUpdates)
                .collect { update ->
                    scope.launch {
                        val ctx = BotContext(client, update, scope)
                        registry.handle(ctx)
                    }
                }
        }

        return this
    }

    /**
     * Drop all pending updates in the queue before starting polling.
     *
     * Useful when you don't want the bot to process old messages that arrived
     * while it was offline. Call this **before** [startPolling] for clean startup.
     *
     * How it works:
     * 1. Fetches the last update with `offset = -1`
     * 2. Sets offset to `lastUpdateId + 1`, effectively skipping all older updates
     * 3. Next `getUpdates` call will only receive new messages
     *
     * This is a **suspend function** — call from coroutine scope or with `runBlocking`.
     *
     * @return This [TelegramBot] instance for method chaining
     *
     * @throws io.telegramkt.exception.TelegramApiException if getUpdates call fails (errors are logged and ignored)
     *
     * Samples:
     * ```kotlin
     * // Clean startup: ignore old messages
     * runBlocking {
     *     telegramBot("TOKEN") {
     *         onCommand("start") { reply("Hi!") }
     *     }
     *     .dropPendingUpdates()  // skip old updates
     *     .startPolling()        // start fresh
     * }
     *
     * // Optional: handle errors explicitly
     * try {
     *     bot.dropPendingUpdates()
     * } catch (e: TelegramApiException) {
     *     println("Warning: could not drop updates: ${e.message}")
     * }
     * ```
     *
     * @see startPolling
     * @see TelegramBotClient.getUpdates
     */
    suspend fun dropPendingUpdates(): TelegramBot {
        try {
            val updates = client.getUpdates(offset = -1, limit = 100, timeout = 0)
            if (updates.isNotEmpty()) {
                val lastUpdateId = updates.last().updateId
                client.getUpdates(offset = lastUpdateId + 1, limit = 1, timeout = 0)
            }
        } catch (e: Exception) {
            println("Error on drop updates: ${e.message}")
        }

        return this
    }

    /**
     * Stop the bot and release all resources.
     *
     * This method:
     * • Cancels the polling coroutine
     * • Closes the underlying HTTP client
     * • Cancels the internal CoroutineScope
     *
     * After calling [stop], the bot instance **cannot be reused**.
     * Create a new instance if you need to restart.
     *
     * Samples:
     * ```kotlin
     * // Graceful shutdown via shutdown hook
     * val bot = telegramBot("TOKEN") { /* config */ }.startPolling()
     *
     * Runtime.getRuntime().addShutdownHook(Thread {
     *     bot.stop()  // clean shutdown on Ctrl+C
     * })
     *
     * // Manual stop
     * bot.stop()
     * // bot.startPolling() // Cannot reuse after stop!
     * ```
     *
     * @see startPolling
     * @see TelegramBotClient.close
     */
    fun stop() {
        pollingJob?.cancel()
        client.close()
        scope.cancel()
        println("Bot stopped")
    }

    suspend fun getMe() = client.getMe()
}