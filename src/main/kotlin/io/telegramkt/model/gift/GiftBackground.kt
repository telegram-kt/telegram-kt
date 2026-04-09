package io.telegramkt.model.gift

import io.telegramkt.model.color.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GiftBackground(
    @SerialName("center_color") val centerColor: Color,
    @SerialName("edge_color") val edgeColor: Color,
    @SerialName("text_color") val textColor: Color,
)
