package io.telegramkt.model.star

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StarAmount(
    @SerialName("amount") val amount: Long,
    @SerialName("nanostar_amount") val nanostarAmount: Int? = null,
) {
    fun toDecimal(): Double = amount + (nanostarAmount ?: 0) / 1_000_000_000.0
}
