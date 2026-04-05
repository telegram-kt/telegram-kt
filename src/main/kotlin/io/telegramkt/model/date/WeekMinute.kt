package io.telegramkt.model.date

import io.telegramkt.serialization.WeekMinuteSerializer
import kotlinx.serialization.Serializable
import java.time.DayOfWeek

@JvmInline
@Serializable(with = WeekMinuteSerializer::class)
value class WeekMinute(val value: Int) {
    init {
        require(value in 0..10080) { "WeekMinute must be in 0..10080, got $value" }
    }

    val dayOfWeek: DayOfWeek get() = DayOfWeek.of((value / 1440) + 1)
    val hour: Int get() = (value % 1440) / 60
    val minute: Int get() = value % 60

    val isMonday: Boolean get() = value < 1440
    val isWeekend: Boolean get() = value >= 5 * 1440

    companion object {
        fun of(day: DayOfWeek, hour: Int, minute: Int): WeekMinute {
            val dayIndex = day.value - 1
            return WeekMinute(dayIndex * 1440 + hour * 60 + minute)
        }

        fun monday(hour: Int = 0, minute: Int = 0) = of(DayOfWeek.MONDAY, hour, minute)
        fun tuesday(hour: Int = 0, minute: Int = 0) = of(DayOfWeek.TUESDAY, hour, minute)
        fun wednesday(hour: Int = 0, minute: Int = 0) = of(DayOfWeek.WEDNESDAY, hour, minute)
        fun thursday(hour: Int = 0, minute: Int = 0) = of(DayOfWeek.THURSDAY, hour, minute)
        fun friday(hour: Int = 0, minute: Int = 0) = of(DayOfWeek.FRIDAY, hour, minute)
        fun saturday(hour: Int = 0, minute: Int = 0) = of(DayOfWeek.SATURDAY, hour, minute)
        fun sunday(hour: Int = 0, minute: Int = 0) = of(DayOfWeek.SUNDAY, hour, minute)
    }
}