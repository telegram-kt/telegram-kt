package io.telegramkt.model.location

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationAddress(
    @SerialName("country_code") val countryCode: String,
    @SerialName("state") val state: String? = null,
    @SerialName("city") val city: String? = null,
    @SerialName("street") val street: String? = null,
)