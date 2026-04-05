package io.telegramkt.model.chat.business

import io.telegramkt.model.date.WeekMinute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Serializable
data class BusinessOpeningHoursInterval(
    @SerialName("opening_minute") val openingMinute: WeekMinute,
    @SerialName("closing_minute") val closingMinute: WeekMinute,
) {
    val duration: Duration get() = (closingMinute.value - openingMinute.value).minutes
    fun contains(minute: WeekMinute): Boolean = minute.value in openingMinute.value..closingMinute.value
}
