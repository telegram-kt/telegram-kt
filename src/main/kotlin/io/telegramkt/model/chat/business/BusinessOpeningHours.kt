package io.telegramkt.model.chat.business

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BusinessOpeningHours(
    @SerialName("time_zone_name") val timeZoneName: String,
    @SerialName("opening_hours") val openingHours: List<BusinessOpeningHoursInterval>,
)
