package io.telegramkt.model.gift.owned.unique

import io.telegramkt.model.color.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UniqueGiftBackdropColors(
    @SerialName("center_color") val centerColor: Color,
    @SerialName("edge_color") val edgeColor: Color,
    @SerialName("symbol_color") val symbolColor: Color,
    @SerialName("text_color") val textColor: Color,
)
