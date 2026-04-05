package io.telegramkt.model.gift

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AcceptedGiftTypes(
    @SerialName("unlimited_gifts") val unlimitedGifts: Boolean,
    @SerialName("limited_gifts") val limitedGifts: Boolean,
    @SerialName("unique_gifts") val uniqueGifts: Boolean,
    @SerialName("premium_subscription") val premiumSubscription: Boolean,
    @SerialName("gifts_from_channels") val giftsFromChannels: Boolean,
)