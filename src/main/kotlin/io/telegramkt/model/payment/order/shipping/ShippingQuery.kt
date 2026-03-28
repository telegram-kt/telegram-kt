package io.telegramkt.model.payment.order.shipping

import io.telegramkt.model.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShippingQuery(
    @SerialName("id") val id: String,
    @SerialName("from") val from: User,
    @SerialName("invoice_payload") val invoicePayload: String,
    @SerialName("shipping_address") val shippingAddress: ShippingAddress,
)
