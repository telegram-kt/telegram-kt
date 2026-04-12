package io.telegramkt.dsl.handlerExt

import io.telegramkt.dsl.BotContext
import io.telegramkt.dsl.HandlerRegistry
import io.telegramkt.model.reaction.ReactionType

fun HandlerRegistry.onAddReaction(
    handler: suspend BotContext.(reaction: ReactionType) -> Unit
) {
    onReaction { update ->
        update.addedReactions().forEach { handler(it) }
    }
}

fun HandlerRegistry.onRemoveReaction(
    handler: suspend BotContext.(reaction: ReactionType) -> Unit
) {
    onReaction { update ->
        update.deletedReactions().forEach { handler(it) }
    }
}

fun HandlerRegistry.onAddReaction(
    reaction: ReactionType,
    handler: suspend BotContext.() -> Unit
) {
    onReaction { update ->
        if (update.wasAdded(reaction)) handler()
    }
}

fun HandlerRegistry.onRemoveReaction(
    reaction: ReactionType,
    handler: suspend BotContext.() -> Unit
) {
    onReaction { update ->
        if (update.wasDeleted(reaction)) handler()
    }
}