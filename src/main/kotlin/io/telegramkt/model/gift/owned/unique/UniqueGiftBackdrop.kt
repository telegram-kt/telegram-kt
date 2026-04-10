package io.telegramkt.model.gift.owned.unique

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UniqueGiftBackdrop(
    @SerialName("name") val name: String,
    @SerialName("colors") val colors: UniqueGiftBackdropColors,
    @SerialName("rarity_per_mille") val rarityPerMille: Int,
)
