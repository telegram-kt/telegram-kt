package io.telegramkt.model.message.input

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InputContactMessageContent(
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("vcard") val vcard: String? = null,
) : InputMessageContent
