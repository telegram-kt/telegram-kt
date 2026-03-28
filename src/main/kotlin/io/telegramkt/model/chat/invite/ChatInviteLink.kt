package io.telegramkt.model.chat.invite

import io.telegramkt.model.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatInviteLink(
    @SerialName("invite_link") val inviteLink: String,
    @SerialName("creator") val creator: User,
    @SerialName("creates_join_request") val createsJoinRequest: Boolean,
    @SerialName("is_primary") val isPrimary: Boolean,
    @SerialName("is_revoked") val isRevoked: Boolean,
    @SerialName("name") val name: String? = null,
    @SerialName("expire_date") val expireDate: Int? = null,
    @SerialName("member_limit") val memberLimit: Int? = null,
    @SerialName("pending_join_request_count") val pendingJoinRequestCount: Int? = null,
    @SerialName("subscription_period") val subscriptionPeriod: Int? = null,
    @SerialName("subscription_price") val subscriptionPrice: Int? = null,
)
