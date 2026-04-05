package io.telegramkt.model.chat

import io.telegramkt.model.audio.Audio
import io.telegramkt.model.chat.administrator.ChatPermissions
import io.telegramkt.model.chat.business.BusinessIntro
import io.telegramkt.model.chat.business.BusinessLocation
import io.telegramkt.model.chat.business.BusinessOpeningHours
import io.telegramkt.model.gift.AcceptedGiftTypes
import io.telegramkt.model.gift.UniqueGiftColors
import io.telegramkt.model.message.Message
import io.telegramkt.model.reaction.ReactionType
import io.telegramkt.model.user.Birthdate
import io.telegramkt.model.user.rating.UserRating
import io.telegramkt.serialization.UnixTimestampSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class ChatFullInfo(
    @SerialName("id") val id: Long,
    @SerialName("type") val type: ChatType,
    @SerialName("title") val title: String? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("is_forum") val isForum: Boolean? = null,
    @SerialName("is_direct_messages") val isDirectMessages: Boolean? = null,
    @SerialName("accent_color_id") val accentColorId: Int,
    @SerialName("max_reaction_count") val maxReactionCount: Int,
    @SerialName("photo") val photo: ChatPhoto? = null,
    @SerialName("active_usernames") val activeUsernames: List<String>? = null,
    @SerialName("birthdate") val birthdate: Birthdate? = null,
    @SerialName("business_intro") val businessIntro: BusinessIntro? = null,
    @SerialName("business_location") val businessLocation: BusinessLocation? = null,
    @SerialName("business_opening_hours") val businessOpeningHours: BusinessOpeningHours? = null,
    @SerialName("personal_chat") val personalChat: Chat? = null,
    @SerialName("parent_chat") val parentChat: Chat? = null,
    @SerialName("available_reactions") val availableReactions: List<ReactionType>? = null,
    @SerialName("background_custom_emoji_id") val backgroundCustomEmojiId: String? = null,
    @SerialName("profile_accent_color_id") val profileAccentColorId: Int? = null,
    @SerialName("profile_background_custom_emoji_id") val profileBackgroundCustomEmojiId: String? = null,
    @SerialName("emoji_status_custom_emoji_id") val emojiStatusCustomEmojiId: String? = null,
    @SerialName("emoji_status_expiration_date")
    @Serializable(with = UnixTimestampSerializer::class)
    val emojiStatusExpirationDate: Instant? = null,
    @SerialName("bio") val bio: String? = null,
    @SerialName("has_private_forwards") val hasPrivateForwards: Boolean? = null,
    @SerialName("has_restricted_voice_and_video_messages") val hasRestrictedVoiceAndVideoMessages: Boolean? = null,
    @SerialName("join_to_send_messages") val joinToSendMessages: Boolean? = null,
    @SerialName("join_by_request") val joinByRequest: Boolean? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("invite_link") val inviteLink: String? = null,
    @SerialName("pinned_message") val pinnedMessage: Message? = null,
    @SerialName("permissions") val permissions: ChatPermissions? = null,
    @SerialName("accepted_gift_types") val acceptedGiftTypes: AcceptedGiftTypes? = null,
    @SerialName("can_send_paid_media") val canSendPaidMedia: Boolean? = null,
    @SerialName("slow_mode_delay") val slowModeDelay: Int? = null,
    @SerialName("unrestrict_boost_count") val unrestrictBoostCount: Int? = null,
    @SerialName("message_auto_delete_time") val messageAutoDeleteTime: Int? = null,
    @SerialName("has_aggressive_anti_spam_enabled") val hasAggressiveAntiSpamEnabled: Boolean? = null,
    @SerialName("has_hidden_members") val hasHiddenMembers: Boolean? = null,
    @SerialName("has_protected_content") val hasProtectedContent: Boolean? = null,
    @SerialName("has_visible_history") val hasVisibleHistory: Boolean? = null,
    @SerialName("sticker_set_name") val stickerSetName: String? = null,
    @SerialName("can_set_sticker_set") val canSetStickerSet: Boolean? = null,
    @SerialName("custom_emoji_sticker_set_name") val customEmojiStickerSetName: String? = null,
    @SerialName("linked_chat_id") val linkedChatId: Long? = null,
    @SerialName("location") val location: ChatLocation? = null,
    @SerialName("rating") val rating: UserRating? = null,
    @SerialName("first_profile_audio") val firstProfileAudio: Audio? = null,
    @SerialName("unique_gift_colors") val uniqueGiftColors: UniqueGiftColors? = null,
    @SerialName("paid_message_star_count") val paidMessageStarCount: Int? = null,
)
