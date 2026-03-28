package io.telegramkt.model.payment.order

import io.telegramkt.model.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PreCheckoutQuery(
    @SerialName("id") val id: String,
    @SerialName("from") val from: User,
    @SerialName("currency") val currency: String,
    @SerialName("total_amount") val totalAmount: Int,
    @SerialName("invoice_payload") val invoicePayload: String,
    @SerialName("shipping_option_id") val shippingOptionId: String? = null,
    @SerialName("order_info") val orderInfo: OrderInfo? = null,
)
