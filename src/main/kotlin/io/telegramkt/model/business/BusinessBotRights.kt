package io.telegramkt.model.business

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BusinessBotRights(
    @SerialName("can_reply") val canReply: Boolean? = null,
    @SerialName("can_read_messages") val canReadMessages: Boolean? = null,
    @SerialName("can_delete_sent_messages") val canDeleteSentMessages: Boolean? = null,
    @SerialName("can_delete_all_messages") val canDeleteAllMessages: Boolean? = null,
    @SerialName("can_edit_name") val canEditName: Boolean? = null,
    @SerialName("can_edit_bio") val canEditBio: Boolean? = null,
    @SerialName("can_edit_profile_photo") val canEditProfilePhoto: Boolean? = null,
    @SerialName("can_edit_username") val canEditUsername: Boolean? = null,
    @SerialName("can_change_gift_settings") val canChangeGiftSettings: Boolean? = null,
    @SerialName("can_view_gifts_and_stars") val canViewGiftsAndStars: Boolean? = null,
    @SerialName("can_convert_gifts_to_stars") val canConvertGiftsToStars: Boolean? = null,
    @SerialName("can_transfer_and_upgrade_gifts") val canTransferAndUpgradeGifts: Boolean? = null,
    @SerialName("can_transfer_stars") val canTransferStars: Boolean? = null,
    @SerialName("can_manage_stories") val canManageStories: Boolean? = null,
)
