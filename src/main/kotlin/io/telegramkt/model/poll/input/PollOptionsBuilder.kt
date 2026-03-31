package io.telegramkt.model.poll.input

import io.telegramkt.model.ParseMode

class PollOptionsBuilder {
    private val options = mutableListOf<InputPollOption>()

    /**
     * Add simple text option.
     *
     * Example: `answer("Yes")`
     */
    fun answer(text: String, block: InputPollOptionBuilder.() -> Unit = {}): PollOptionsBuilder {
        options.add(InputPollOptionBuilder(text).apply(block).build())
        return this
    }

    /**
     * Add option with HTML formatting.
     *
     * Example: `answerHtml("<b>Yes</b>")`
     */
    fun answerHtml(text: String): PollOptionsBuilder {
        options.add(
            InputPollOptionBuilder(text)
                .apply { textParseMode = ParseMode.HTML }
                .build()
        )
        return this
    }

    /**
     * Add option with Markdown formatting.
     *
     * Example: `answerMarkdown("*Yes*")`
     */
    fun answerMarkdown(text: String): PollOptionsBuilder {
        options.add(
            InputPollOptionBuilder(text)
                .apply { textParseMode = ParseMode.MARKDOWN }
                .build()
        )
        return this
    }

    internal fun build(): List<InputPollOption> {
        require(options.size >= 2) { "Poll must have at least 2 options" }
        require(options.size <= 10) { "Poll must have at most 10 options" }
        return options.toList()
    }

    companion object {
        fun build(block: PollOptionsBuilder.() -> Unit): List<InputPollOption> {
            return PollOptionsBuilder().apply(block).build()
        }
    }
}