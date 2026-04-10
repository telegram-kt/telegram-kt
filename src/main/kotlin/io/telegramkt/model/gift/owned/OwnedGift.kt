package io.telegramkt.model.gift.owned

import io.telegramkt.model.gift.Gift
import io.telegramkt.model.gift.owned.unique.UniqueGift
import io.telegramkt.model.message.entity.MessageEntity
import io.telegramkt.model.user.User
import io.telegramkt.serialization.UnixTimestampSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
sealed class OwnedGift {
    abstract val senderUser: User?
    abstract val sendDate: Instant
    abstract val isSaved: Boolean?

    @Serializable
    @SerialName("regular")
    data class Regular(
        @SerialName("gift") val gift: Gift,
        @SerialName("owned_gift_id") val ownedGiftId: String? = null,
        @SerialName("sender_user") override val senderUser: User? = null,
        @SerialName("send_date")
        @Serializable(with = UnixTimestampSerializer::class)
        override val sendDate: Instant,
        @SerialName("text") val text: String? = null,
        @SerialName("entities") val entities: List<MessageEntity>? = null,
        @SerialName("is_private") val isPrivate: Boolean? = null,
        @SerialName("is_saved") override val isSaved: Boolean? = null,
        @SerialName("can_be_upgraded") val canBeUpgraded: Boolean? = null,
        @SerialName("was_refunded") val wasRefunded: Boolean? = null,
        @SerialName("convert_star_count") val convertStarCount: Int? = null,
        @SerialName("prepaid_upgrade_star_count") val prepaidUpgradeStarCount: Int? = null,
        @SerialName("is_upgrade_separate") val isUpgradeSeparate: Boolean? = null,
        @SerialName("unique_gift_number") val uniqueGiftNumber: Int? = null,
    ): OwnedGift()

    @Serializable
    @SerialName("unique")
    data class Unique(
        @SerialName("gift") val gift: UniqueGift,
        @SerialName("owned_gift_id") val ownedGiftId: String? = null,
        @SerialName("sender_user") override val senderUser: User? = null,
        @SerialName("send_date")
        @Serializable(with = UnixTimestampSerializer::class)
        override val sendDate: Instant,
        @SerialName("is_saved") override val isSaved: Boolean? = null,
        @SerialName("can_be_transferred") val canBeTransferred: Boolean? = null,
        @SerialName("transfer_star_count") val transferStarCount: Int? = null,
        @SerialName("next_transfer_date")
        @Serializable(with = UnixTimestampSerializer::class)
        val nextTransferDate: Instant? = null,
    ): OwnedGift()
}