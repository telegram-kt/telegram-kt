package io.telegramkt.model.bot.command

class BotCommandBuilder {
    private val commands = mutableListOf<BotCommand>()

    /**
     * Add command with description.
     *
     * Command must:
     * - Start with lowercase letter
     * - Contain only lowercase letters, digits and underscores
     * - Be 1-32 chars
     *
     * Description: 1-256 chars
     */
    fun command(name: String, description: String) {
        require(name.matches(Regex("^[a-z][a-z0-9_]{0,31}$"))) {
            "Command '$name' must start with lowercase letter and contain only a-z, 0-9, underscores"
        }
        require(description.length in 1..256) {
            "Description must be 1-256 chars"
        }
        commands.add(BotCommand(name, description))
    }

    fun commands(vararg pairs: Pair<String, String>) {
        pairs.forEach { (name, desc) ->
            command(name, desc)
        }
    }

    internal fun build(): List<BotCommand> {
        require(commands.isNotEmpty()) { "At least one command required." }
        require(commands.size <= 100) { "Max 100 commands allowed." }
        return commands.toList()
    }
}