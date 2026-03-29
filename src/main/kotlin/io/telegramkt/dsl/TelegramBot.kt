package io.telegramkt.dsl

import io.telegramkt.client.TelegramBotClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class TelegramBot(
    private val client: TelegramBotClient,
    private val registry: HandlerRegistry,
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var pollingJob: Job? = null

    fun startPolling(
        limit: Int = 100,
        timeout: Int = 30,
        allowedUpdates: List<String>? = null,
    ) {
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
    }

    suspend fun dropPendingUpdates() {
        try {
            val updates = client.getUpdates(offset = -1, limit = 100, timeout = 0)
            if (updates.isNotEmpty()) {
                val lastUpdateId = updates.last().updateId
                client.getUpdates(offset = lastUpdateId + 1, limit = 1, timeout = 0)
            }
        } catch (e: Exception) {
            println("Error on drop updates: ${e.message}")
        }
    }

    fun stop() {
        pollingJob?.cancel()
        client.close()
        scope.cancel()
        println("Bot stopped")
    }

    suspend fun getMe() = client.getMe()
}