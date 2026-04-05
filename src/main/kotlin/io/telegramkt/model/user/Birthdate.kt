package io.telegramkt.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Birthdate(
    @SerialName("day") val day: Int,
    @SerialName("month") val month: Int,
    @SerialName("year") val year: Int? = null,
)
