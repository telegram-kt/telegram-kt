package io.telegramkt.model.gift.owned.unique

import io.telegramkt.model.sticker.Sticker
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UniqueGiftModel(
    @SerialName("name") val name: String,
    @SerialName("sticker") val sticker: Sticker,
    @SerialName("rarity_per_mille") val rarityPerMille: Int,
    @SerialName("rarity") val rarity: UniqueGiftRarity,
)
