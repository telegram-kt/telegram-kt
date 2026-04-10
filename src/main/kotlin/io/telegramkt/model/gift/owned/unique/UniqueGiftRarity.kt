package io.telegramkt.model.gift.owned.unique

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class UniqueGiftRarity {
    @SerialName("uncommon") UNCOMMON,
    @SerialName("rare") RARE,
    @SerialName("epic") EPIC,
    @SerialName("legendary") LEGENDARY,
}