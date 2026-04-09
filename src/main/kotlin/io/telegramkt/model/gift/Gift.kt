package io.telegramkt.model.gift

import io.telegramkt.model.chat.Chat
import io.telegramkt.model.sticker.Sticker
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Gift(
    @SerialName("id") val id: String,
    @SerialName("sticker") val sticker: Sticker,
    @SerialName("star_count") val starCount: Int,
    @SerialName("upgrade_star_count") val upgradeStarCount: Int? = null,
    @SerialName("is_premium") val isPremium: Boolean? = null,
    @SerialName("has_colors") val hasColors: Boolean? = null,
    @SerialName("total_count") val totalCount: Int? = null,
    @SerialName("remaining_count") val remainingCount: Int? = null,
    @SerialName("personal_total_count") val personalTotalCount: Int? = null,
    @SerialName("personal_remaining_count") val personalRemainingCount: Int? = null,
    @SerialName("background") val background: GiftBackground? = null,
    @SerialName("unique_gift_variant_count") val uniqueGiftVariantCount: Int? = null,
    @SerialName("publisher_chat") val publisherChat: Chat? = null,
)
