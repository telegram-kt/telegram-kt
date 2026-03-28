package io.telegramkt.model.keyboard.button

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KeyboardButtonRequestUsers(
    @SerialName("request_id") val requestId: Int,
    @SerialName("user_is_bot") val userIsBot: Boolean? = null,
    @SerialName("user_is_premium") val userIsPremium: Boolean? = null,
    @SerialName("max_quantity") val maxQuantity: Int? = null,
    @SerialName("request_name") val requestName: Boolean? = null,
    @SerialName("request_username") val requestUsername: Boolean? = null,
    @SerialName("request_photo") val requestPhoto: Boolean? = null
)
