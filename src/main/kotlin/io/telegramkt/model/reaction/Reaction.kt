package io.telegramkt.model.reaction

object Reaction {
    val ALLOWED_EMOJIS = setOf(
        "❤", "👍", "👎", "🔥", "🥰", "👏", "😁", "🤔", "🤯",
        "😱", "🤬", "😢", "🎉", "🤩", "🤮", "💩", "🙏", "👌",
        "🕊", "🤡", "🥱", "🥴", "😍", "🐳", "❤‍🔥", "🌚", "🌭",
        "💯", "🤣", "⚡", "🍌", "🏆", "💔", "🤨", "😐", "🍓", "🍾",
        "💋", "🖕", "😈", "😴", "😭", "🤓", "👻", "👨‍💻", "👀", "🎃",
        "🙈", "😇", "😨", "🤝", "✍", "🤗", "🫡", "🎅", "🎄", "☃", "💅",
        "🤪", "🗿", "🆒", "💘", "🙉", "🦄", "😘", "💊", "🙊", "😎",
        "👾", "🤷‍♂", "🤷", "🤷‍♀", "😡"
    )

    fun emoji(emoji: String): ReactionType.Emoji {
        require(emoji in ALLOWED_EMOJIS) {
            "Emoji '$emoji' is not allowed by Telegram API. Use one of: ${ALLOWED_EMOJIS.joinToString()}"
        }
        return ReactionType.Emoji(emoji)
    }

    fun emojiUnsafe(emoji: String): ReactionType.Emoji = ReactionType.Emoji(emoji)

    fun custom(customEmojiId: String): ReactionType.CustomEmoji = ReactionType.CustomEmoji(customEmojiId)

    val paid: ReactionType.Paid = ReactionType.Paid
}