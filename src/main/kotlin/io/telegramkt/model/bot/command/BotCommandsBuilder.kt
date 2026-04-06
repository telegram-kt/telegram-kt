package io.telegramkt.model.bot.command

class BotCommandsBuilder {
    private val scopedCommands = mutableMapOf<Pair<BotCommandScope, String?>, List<BotCommand>>()

    /**
     * Set commands for default scope (all users, all chats).
     */
    fun default(languageCode: String? = null, block: BotCommandBuilder.() -> Unit) {
        scopedCommands[BotCommandScope.Default to languageCode] = BotCommandBuilder().apply(block).build()
    }

    /**
     * Set commands for all private chats.
     */
    fun allPrivateChats(languageCode: String? = null, block: BotCommandBuilder.() -> Unit) {
        scopedCommands[BotCommandScope.AllPrivateChats to languageCode] = BotCommandBuilder().apply(block).build()
    }

    /**
     * Set commands for all group chats.
     */
    fun allGroupChats(languageCode: String? = null, block: BotCommandBuilder.() -> Unit) {
        scopedCommands[BotCommandScope.AllGroupChats to languageCode] = BotCommandBuilder().apply(block).build()
    }

    /**
     * Set commands for all chat administrators.
     */
    fun allChatAdministrators(languageCode: String? = null, block: BotCommandBuilder.() -> Unit) {
        scopedCommands[BotCommandScope.AllChatAdministrators to languageCode] = BotCommandBuilder().apply(block).build()
    }

    /**
     * Set commands for specific chat.
     */
    fun chat(chatId: Long, languageCode: String? = null, block: BotCommandBuilder.() -> Unit) {
        scopedCommands[BotCommandScope.Chat(chatId) to languageCode] = BotCommandBuilder().apply(block).build()
    }

    /**
     * Set commands for chat administrators.
     */
    fun chatAdministrators(chatId: Long, languageCode: String? = null, block: BotCommandBuilder.() -> Unit) {
        scopedCommands[BotCommandScope.ChatAdministrators(chatId) to languageCode] = BotCommandBuilder().apply(block).build()
    }

    /**
     * Set commands for specific chat member.
     */
    fun chatMember(chatId: Long, userId: Long, languageCode: String? = null, block: BotCommandBuilder.() -> Unit) {
        scopedCommands[BotCommandScope.ChatMember(chatId, userId) to languageCode] = BotCommandBuilder().apply(block).build()
    }

    internal fun build(): List<ScopedCommands> {
        return scopedCommands.map { (key, commands) ->
            ScopedCommands(key.first, key.second, commands)
        }
    }

    data class ScopedCommands(
        val scope: BotCommandScope,
        val languageCode: String?,
        val commands: List<BotCommand>
    )
}

fun botCommands(block: BotCommandsBuilder.() -> Unit): List<BotCommandsBuilder.ScopedCommands> {
    return BotCommandsBuilder().apply(block).build()
}