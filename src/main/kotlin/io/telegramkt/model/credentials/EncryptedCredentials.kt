package io.telegramkt.model.credentials

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EncryptedCredentials(
    @SerialName("data") val data: String,
    @SerialName("hash") val hash: String,
    @SerialName("secret") val secret: String
)
