package io.telegramkt.model.poll

import io.telegramkt.serialization.PollTypeSerializer
import kotlinx.serialization.Serializable

@Serializable(with = PollTypeSerializer::class)
enum class PollType {
    QUIZ,
    REGULAR
}