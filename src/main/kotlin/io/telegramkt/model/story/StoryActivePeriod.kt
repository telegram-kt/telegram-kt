package io.telegramkt.model.story

import io.telegramkt.serialization.story.StoryActivePeriodSerializer
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

@Serializable(with = StoryActivePeriodSerializer::class)
enum class StoryActivePeriod(val duration: Duration) {
    SIX_HOURS(6.hours),
    TWELVE_HOURS(12.hours),
    ONE_DAY(1.days),
    TWO_DAYS(2.days);

    companion object {
        fun fromDuration(duration: Duration): StoryActivePeriod = when (duration) {
            6.hours -> SIX_HOURS
            12.hours -> TWELVE_HOURS
            1.days -> ONE_DAY
            2.days -> TWO_DAYS
            else -> throw IllegalArgumentException(
                "Active period must be 6h, 12h, 1d or 2d, got $duration"
            )
        }
    }
}