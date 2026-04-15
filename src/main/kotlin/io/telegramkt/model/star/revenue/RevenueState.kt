package io.telegramkt.model.star.revenue

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This object describes the state of a revenue withdrawal operation.
 *
 * See https://core.telegram.org/bots/api#revenuestate
 */
@Serializable
sealed class RevenueState {

    @Serializable
    @SerialName("pending")
    data object Pending : RevenueState()

    @Serializable
    @SerialName("succeeded")
    data class Succeeded(
        @SerialName("date") val date: Int,
    ) : RevenueState()
}