package io.telegramkt.model.gift

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Gifts(@SerialName("gifts") val gifts: List<Gift>) {
    operator fun invoke(): List<Gift> = gifts
}