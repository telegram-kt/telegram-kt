package io.telegramkt.model.gift.owned.unique

import io.telegramkt.model.chat.Chat
import io.telegramkt.model.gift.UniqueGiftColors
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UniqueGift(
    @SerialName("gift_id") val giftId: String,
    @SerialName("base_name") val baseName: String,
    @SerialName("name") val name: String,
    @SerialName("number") val number: String,
    @SerialName("model") val model: UniqueGiftModel,
    @SerialName("symbol") val symbol: UniqueGiftSymbol,
    @SerialName("backdrop") val backdrop: UniqueGiftBackdrop,
    @SerialName("is_premium") val isPremium: Boolean? = null,
    @SerialName("is_burned") val isBurned: Boolean? = null,
    @SerialName("is_from_blockchain") val isFromBlockchain: Boolean? = null,
    @SerialName("colors") val colors: UniqueGiftColors? = null,
    @SerialName("publisher_chat") val publisherChat: Chat? = null,
)