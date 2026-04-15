package io.telegramkt.model.star

import io.telegramkt.model.media.input.PaidMedia
import io.telegramkt.model.star.revenue.RevenueState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This object describes the number of Telegram Stars that need to be transferred
 * in a transaction, and the partner for the transaction.
 *
 * See https://core.telegram.org/bots/api#transactionpartner
 */
@Serializable
sealed class TransactionPartner {

    @Serializable
    @SerialName("fragment")
    data class Fragment(
        @SerialName("withdrawal_state") val withdrawalState: RevenueState? = null,
    ) : TransactionPartner()

    @Serializable
    @SerialName("user")
    data class User(
        @SerialName("user") val user: io.telegramkt.model.user.User,
        @SerialName("invoice_payload") val invoicePayload: String? = null,
        @SerialName("paid_media") val paidMedia: List<PaidMedia>? = null,
    ) : TransactionPartner()

    @Serializable
    @SerialName("other")
    data object Other : TransactionPartner()

    @Serializable
    @SerialName("telegram_ads")
    data object TelegramAds : TransactionPartner()

    @Serializable
    @SerialName("telegram_api")
    data class TelegramApi(
        @SerialName("request_count") val requestCount: Int? = null,
    ) : TransactionPartner()

    @Serializable
    @SerialName("business")
    data class Business(
        @SerialName("business_connection_id") val businessConnectionId: String,
        @SerialName("invoice_payload") val invoicePayload: String? = null,
        @SerialName("paid_media") val paidMedia: List<PaidMedia>? = null,
    ) : TransactionPartner()
}


