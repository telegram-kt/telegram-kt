package io.telegramkt.model.reaction

class ReactionBuilder {
    private val _reactions: MutableList<ReactionType> = mutableListOf()
    val reactions: List<ReactionType> get() = _reactions

    fun reaction(reaction: ReactionType): ReactionBuilder {
        _reactions.add(reaction)
        return this
    }

    fun emoji(emoji: String): ReactionBuilder = reaction(Reaction.emoji(emoji))

    fun emojiUnsafe(emoji: String): ReactionBuilder = reaction(Reaction.emojiUnsafe(emoji))

    fun custom(customEmojiId: String): ReactionBuilder = reaction(Reaction.custom(customEmojiId))

    fun paid(): ReactionBuilder = reaction(Reaction.paid)

    fun reactions(vararg reactions: ReactionType): ReactionBuilder {
        _reactions.addAll(reactions)
        return this
    }

    fun clear(): ReactionBuilder {
        _reactions.clear()
        return this
    }
}

fun reactions(block: ReactionBuilder.() -> Unit): List<ReactionType> {
    return ReactionBuilder().apply(block).reactions
}