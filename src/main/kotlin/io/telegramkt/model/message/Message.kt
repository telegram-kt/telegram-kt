package io.telegramkt.model.message

import io.telegramkt.model.contact.Contact
import io.telegramkt.model.LinkPreviewOptions
import io.telegramkt.model.animation.Animation
import io.telegramkt.model.audio.Audio
import io.telegramkt.model.audio.Voice
import io.telegramkt.model.chat.Chat
import io.telegramkt.model.chat.background.ChatBackground
import io.telegramkt.model.chat.boost.ChatBoostAdded
import io.telegramkt.model.chat.shared.ChatShared
import io.telegramkt.model.chat.video.VideoChatEnded
import io.telegramkt.model.chat.video.VideoChatParticipantsInvited
import io.telegramkt.model.chat.video.VideoChatScheduled
import io.telegramkt.model.chat.video.VideoChatStarted
import io.telegramkt.model.checklist.Checklist
import io.telegramkt.model.document.Document
import io.telegramkt.model.forum.ForumTopicClosed
import io.telegramkt.model.forum.ForumTopicCreated
import io.telegramkt.model.forum.ForumTopicEdited
import io.telegramkt.model.forum.ForumTopicReopened
import io.telegramkt.model.forum.GeneralForumTopicHidden
import io.telegramkt.model.forum.GeneralForumTopicUnhidden
import io.telegramkt.model.game.Dice
import io.telegramkt.model.game.Game
import io.telegramkt.model.giveaway.Giveaway
import io.telegramkt.model.giveaway.GiveawayCompleted
import io.telegramkt.model.giveaway.GiveawayCreated
import io.telegramkt.model.giveaway.GiveawayWinners
import io.telegramkt.model.keyboard.reply.InlineKeyboardMarkup
import io.telegramkt.model.payment.Invoice
import io.telegramkt.model.location.Location
import io.telegramkt.model.location.Venue
import io.telegramkt.model.message.entity.MessageEntity
import io.telegramkt.model.passport.PassportData
import io.telegramkt.model.payment.RefundedPayment
import io.telegramkt.model.payment.SuccessfulPayment
import io.telegramkt.model.photo.PhotoSize
import io.telegramkt.model.poll.Poll
import io.telegramkt.model.proximity.ProximityAlertTriggered
import io.telegramkt.model.sticker.Sticker
import io.telegramkt.model.user.User
import io.telegramkt.model.user.shared.UsersShared
import io.telegramkt.model.video.Story
import io.telegramkt.model.video.Video
import io.telegramkt.model.video.VideoNote
import io.telegramkt.model.web.WebAppData
import io.telegramkt.model.writeaccess.WriteAccessAllowed
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    @SerialName("message_id") val id: Int,
    @SerialName("message_thread_id") val threadId: Int? = null,
    @SerialName("from") val from: User? = null,
    @SerialName("sender_chat") val senderChat: Chat? = null,
    @SerialName("sender_boost_count") val senderBoostCount: Int? = null,
    @SerialName("date") val date: Int,
    @SerialName("chat") val chat: Chat,
    @SerialName("forward_origin") val forwardOrigin: MessageOrigin? = null,
    @SerialName("is_topic_message") val isTopicMessage: Boolean? = null,
    @SerialName("is_automatic_forward") val isAutomaticForward: Boolean? = null,
    @SerialName("reply_to_message") val replyToMessage: Message? = null,
    @SerialName("via_bot") val viaBot: User? = null,
    @SerialName("edit_date") val editDate: Int? = null,
    @SerialName("has_protected_content") val hasProtectedContent: Boolean? = null,
    @SerialName("media_group_id") val mediaGroupId: String? = null,
    @SerialName("author_signature") val authorSignature: String? = null,
    @SerialName("text") val text: String? = null,
    @SerialName("entities") val entities: List<MessageEntity>? = null,
    @SerialName("link_preview_options") val linkPreviewOptions: LinkPreviewOptions? = null,
    @SerialName("effect_id") val effectId: String? = null,
    @SerialName("animation") val animation: Animation? = null,
    @SerialName("audio") val audio: Audio? = null,
    @SerialName("document") val document: Document? = null,
    @SerialName("photo") val photo: List<PhotoSize>? = null,
    @SerialName("sticker") val sticker: Sticker? = null,
    @SerialName("story") val story: Story? = null,
    @SerialName("video") val video: Video? = null,
    @SerialName("video_note") val videoNote: VideoNote? = null,
    @SerialName("voice") val voice: Voice? = null,
    @SerialName("caption") val caption: String? = null,
    @SerialName("caption_entities") val captionEntities: List<MessageEntity>? = null,
    @SerialName("has_media_spoiler") val hasMediaSpoiler: Boolean? = null,
    @SerialName("checklist") val checklist: Checklist? = null,
    @SerialName("contact") val contact: Contact? = null,
    @SerialName("dice") val dice: Dice? = null,
    @SerialName("game") val game: Game? = null,
    @SerialName("poll") val poll: Poll? = null,
    @SerialName("venue") val venue: Venue? = null,
    @SerialName("location") val location: Location? = null,
    @SerialName("new_chat_members") val newChatMembers: List<User>? = null,
    @SerialName("left_chat_member") val leftChatMember: User? = null,
    @SerialName("new_chat_title") val newChatTitle: String? = null,
    @SerialName("new_chat_photo") val newChatPhoto: List<PhotoSize>? = null,
    @SerialName("delete_chat_photo") val deleteChatPhoto: Boolean? = null,
    @SerialName("group_chat_created") val groupChatCreated: Boolean? = null,
    @SerialName("supergroup_chat_created") val supergroupChatCreated: Boolean? = null,
    @SerialName("channel_chat_created") val channelChatCreated: Boolean? = null,
    @SerialName("message_auto_delete_timer_changed") val messageAutoDeleteTimerChanged: MessageAutoDeleteTimerChanged? = null,
    @SerialName("migrate_to_chat_id") val migrateToChatId: Long? = null,
    @SerialName("migrate_from_chat_id") val migrateFromChatId: Long? = null,
    @SerialName("pinned_message") val pinnedMessage: MaybeInaccessibleMessage? = null,
    @SerialName("invoice") val invoice: Invoice? = null,
    @SerialName("successful_payment") val successfulPayment: SuccessfulPayment? = null,
    @SerialName("refunded_payment") val refundedPayment: RefundedPayment? = null,
    @SerialName("users_shared") val usersShared: UsersShared? = null,
    @SerialName("chat_shared") val chatShared: ChatShared? = null,
    @SerialName("connected_website") val connectedWebsite: String? = null,
    @SerialName("write_access_allowed") val writeAccessAllowed: WriteAccessAllowed? = null,
    @SerialName("passport_data") val passportData: PassportData? = null,
    @SerialName("proximity_alert_triggered") val proximityAlertTriggered: ProximityAlertTriggered? = null,
    @SerialName("boost_added") val boostAdded: ChatBoostAdded? = null,
    @SerialName("chat_background_set") val chatBackgroundSet: ChatBackground? = null,
    @SerialName("forum_topic_created") val forumTopicCreated: ForumTopicCreated? = null,
    @SerialName("forum_topic_edited") val forumTopicEdited: ForumTopicEdited? = null,
    @SerialName("forum_topic_closed") val forumTopicClosed: ForumTopicClosed? = null,
    @SerialName("forum_topic_reopened") val forumTopicReopened: ForumTopicReopened? = null,
    @SerialName("general_forum_topic_hidden") val generalForumTopicHidden: GeneralForumTopicHidden? = null,
    @SerialName("general_forum_topic_unhidden") val generalForumTopicUnhidden: GeneralForumTopicUnhidden? = null,
    @SerialName("giveaway_created") val giveawayCreated: GiveawayCreated? = null,
    @SerialName("giveaway") val giveaway: Giveaway? = null,
    @SerialName("giveaway_winners") val giveawayWinners: GiveawayWinners? = null,
    @SerialName("giveaway_completed") val giveawayCompleted: GiveawayCompleted? = null,
    @SerialName("video_chat_scheduled") val videoChatScheduled: VideoChatScheduled? = null,
    @SerialName("video_chat_started") val videoChatStarted: VideoChatStarted? = null,
    @SerialName("video_chat_ended") val videoChatEnded: VideoChatEnded? = null,
    @SerialName("video_chat_participants_invited") val videoChatParticipantsInvited: VideoChatParticipantsInvited? = null,
    @SerialName("web_app_data") val webAppData: WebAppData? = null,
    @SerialName("reply_markup") val replyMarkup: InlineKeyboardMarkup? = null,
) {
    val isEdited: Boolean get() = editDate != null
    val isForwarded: Boolean get() = forwardOrigin != null
}
