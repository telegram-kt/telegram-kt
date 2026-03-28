package io.telegramkt.model.payment

import io.telegramkt.model.payment.order.OrderInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuccessfulPayment(
    @SerialName("currency") val currency: String,
    @SerialName("total_amount") val totalAmount: Int,
    @SerialName("invoice_payload") val invoicePayload: String,
    @SerialName("subscription_expiration_date") val subscriptionExpirationDate: Int? = null,
    @SerialName("is_recurring") val isRecurring: Boolean? = null,
    @SerialName("is_first_recurring") val isFirstRecurring: Boolean? = null,
    @SerialName("shipping_option_id") val shippingOptionId: String? = null,
    @SerialName("order_info") val orderInfo: OrderInfo? = null,
    @SerialName("telegram_payment_charge_id") val telegramPaymentChargeId: String,
    @SerialName("provider_payment_charge_id") val providerPaymentChargeId: String,
)
