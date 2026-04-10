package io.telegramkt.model.gift.owned

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OwnedGifts(
    @SerialName("total_count") val totalCount: Int,
    @SerialName("gifts") val gifts: List<OwnedGift>,
    @SerialName("next_offset") val nextOffset: String? = null,
)