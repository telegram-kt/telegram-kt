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
import io.telegramkt.model.gift.Gift
import io.telegramkt.model.giveaway.Giveaway
import io.telegramkt.model.giveaway.GiveawayCompleted
import io.telegramkt.model.giveaway.GiveawayCreated
import io.telegramkt.model.giveaway.GiveawayWinners
import io.telegramkt.model.keyboard.reply.InlineKeyboardMarkup
import io.telegramkt.model.payment.Invoice
import io.telegramkt.model.payment.RefundedPayment
import io.telegramkt.model.payment.SuccessfulPayment
import io.telegramkt.model.location.Location
import io.telegramkt.model.location.Venue
import io.telegramkt.model.message.entity.MessageEntity
import io.telegramkt.model.passport.PassportData
import io.telegramkt.model.proximity.ProximityAlertTriggered
import io.telegramkt.model.reaction.ReactionType
import io.telegramkt.model.star.TransactionPartner
import io.telegramkt.model.sticker.Sticker
import io.telegramkt.model.user.User
import io.telegramkt.model.user.shared.UsersShared
import io.telegramkt.model.story.Story
import io.telegramkt.model.suggested.SuggestedTopic
import io.telegramkt.model.video.Video
import io.telegramkt.model.video.VideoNote
import io.telegramkt.model.web.ExternalReplyInfo
import io.telegramkt.model.web.WebAppData
import io.telegramkt.model.message.TextQuote
import io.telegramkt.model.writeaccess.WriteAccessAllowed
import io.telegramkt.serialization.UnixTimestampSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * This object represents a message.
 *
 * See https://core.telegram.org/bots/api#message
 */
@Serializable
data class Message(
    /**
     * Unique message identifier inside this chat
     */
    @SerialName("message_id") val id: Int,
    /**
     * Optional. Unique identifier of a message thread to which the message belongs; for supergroups only
     */
    @SerialName("message_thread_id") val threadId: Int? = null,
    /**
     * Optional. Sender of the message; empty for messages sent to channels.
     * For backward compatibility, the field contains a fake sender user in non-channel chats,
     * if the message was sent on behalf of a chat.
     */
    @SerialName("from") val from: User? = null,
    /**
     * Optional. Sender of the message, sent on behalf of a chat.
     * For example: the channel itself for channel posts, a supergroup itself for messages
     * from anonymous group administrators, the linked channel for messages automatically
     * forwarded to the discussion group. For backward compatibility, the field `from` contains
     * a fake sender user in non-channel chats, if the message was sent on behalf of a chat.
     */
    @SerialName("sender_chat") val senderChat: Chat? = null,
    /**
     * Optional. If the sender of the message boosted the chat, the number of boosts added by the user
     */
    @SerialName("sender_boost_count") val senderBoostCount: Int? = null,
    /**
     * Optional. The bot that actually sent the message on behalf of the business account.
     * Available only for outgoing messages sent on behalf of the connected business account.
     */
    @SerialName("sender_business_bot") val senderBusinessBot: User? = null,
    /**
     * Date the message was sent.
     */
    @SerialName("date")
    @Serializable(with = UnixTimestampSerializer::class)
    val date: Instant,
    /**
     * Optional. Unique identifier of the business connection from which the message was received.
     * If non-empty, the message belongs to a specific connection of the business account.
     */
    @SerialName("business_connection_id") val businessConnectionId: String? = null,
    /**
     * Conversation the message belongs to
     */
    @SerialName("chat") val chat: Chat,
    /**
     * Optional. Information about the original message for forwarded messages
     */
    @SerialName("forward_origin") val forwardOrigin: MessageOrigin? = null,
    /**
     * Optional. True, if the message is sent to a forum topic
     */
    @SerialName("is_topic_message") val isTopicMessage: Boolean? = null,
    /**
     * Optional. True, if the message is a channel post that was automatically forwarded
     * to the connected discussion group
     */
    @SerialName("is_automatic_forward") val isAutomaticForward: Boolean? = null,
    /**
     * Optional. For replies in the same chat and message thread, the original message.
     * Note that the Message object in this field will not contain further reply_to_message fields
     * even if it itself is a reply.
     */
    @SerialName("reply_to_message") val replyToMessage: Message? = null,
    /**
     * Optional. Information about the message that is being replied to, which may come from another
     * chat or forum topic
     */
    @SerialName("external_reply") val externalReply: ExternalReplyInfo? = null,
    /**
     * Optional. For replies that quote part of the original message, the part of the original message
     * that is quoted
     */
    @SerialName("quote") val quote: TextQuote? = null,
    /**
     * Optional. For replies to a story, the original story
     */
    @SerialName("reply_to_story") val replyToStory: Story? = null,
    /**
     * Optional. Bot through which the message was sent
     */
    @SerialName("via_bot") val viaBot: User? = null,
    /**
     * Optional. Date the message was last edited.
     */
    @SerialName("edit_date")
    @Serializable(with = UnixTimestampSerializer::class)
    val editDate: Instant? = null,
    /**
     * Optional. True, if the message can't be forwarded
     */
    @SerialName("has_protected_content") val hasProtectedContent: Boolean? = null,
    /**
     * Optional. True, if the message was sent by an implicit action, for example:
     * as an away or a greeting business message, or as a scheduled message
     */
    @SerialName("is_from_offline") val isFromOffline: Boolean? = null,
    /**
     * Optional. The unique identifier of the media group this message belongs to
     */
    @SerialName("media_group_id") val mediaGroupId: String? = null,
    /**
     * Optional. Signature of the post author for messages in channels, or the custom title
     * of an anonymous group administrator
     */
    @SerialName("author_signature") val authorSignature: String? = null,
    /**
     * Optional. For text messages, the actual UTF-8 text of the message
     */
    @SerialName("text") val text: String? = null,
    /**
     * Optional. For text messages, special entities like usernames, URLs, bot commands, etc.
     * that appear in the text
     */
    @SerialName("entities") val entities: List<MessageEntity>? = null,
    /**
     * Optional. Options used for link preview generation for the message, if it is a text message
     * and link preview options were changed
     */
    @SerialName("link_preview_options") val linkPreviewOptions: LinkPreviewOptions? = null,
    /**
     * Optional. Unique identifier of the message effect added to the message
     */
    @SerialName("effect_id") val effectId: String? = null,
    /**
     * Optional. Message is an animation, information about the animation.
     * For backward compatibility, when this field is set, the document field will also be set
     */
    @SerialName("animation") val animation: Animation? = null,
    /**
     * Optional. Message is an audio file, information about the file
     */
    @SerialName("audio") val audio: Audio? = null,
    /**
     * Optional. Message is a general file, information about the file
     */
    @SerialName("document") val document: Document? = null,
    /**
     * Optional. Message is a photo, available sizes of the photo
     */
    @SerialName("photo") val photo: List<io.telegramkt.model.photo.PhotoSize>? = null,
    /**
     * Optional. Message is a sticker, information about the sticker
     */
    @SerialName("sticker") val sticker: Sticker? = null,
    /**
     * Optional. Message is a forwarded story
     */
    @SerialName("story") val story: Story? = null,
    /**
     * Optional. Message is a video, information about the video
     */
    @SerialName("video") val video: Video? = null,
    /**
     * Optional. Message is a video note, information about the video message
     */
    @SerialName("video_note") val videoNote: VideoNote? = null,
    /**
     * Optional. Message is a voice message, information about the file
     */
    @SerialName("voice") val voice: Voice? = null,
    /**
     * Optional. Caption for the animation, audio, document, photo, video or voice
     */
    @SerialName("caption") val caption: String? = null,
    /**
     * Optional. For messages with a caption, special entities like usernames, URLs, bot commands,
     * etc. that appear in the caption
     */
    @SerialName("caption_entities") val captionEntities: List<MessageEntity>? = null,
    /**
     * Optional. True, if the caption must be shown above the message media
     */
    @SerialName("show_caption_above_media") val showCaptionAboveMedia: Boolean? = null,
    /**
     * Optional. True, if the message media is covered by a spoiler animation
     */
    @SerialName("has_media_spoiler") val hasMediaSpoiler: Boolean? = null,
    /**
     * Optional. Message is a shared checklist, information about the checklist
     */
    @SerialName("checklist") val checklist: Checklist? = null,
    /**
     * Optional. Message is a shared contact, information about the contact
     */
    @SerialName("contact") val contact: Contact? = null,
    /**
     * Optional. Message is a dice with random value
     */
    @SerialName("dice") val dice: Dice? = null,
    /**
     * Optional. Message is a game, information about the game
     */
    @SerialName("game") val game: Game? = null,
    /**
     * Optional. Message is a native poll, information about the poll
     */
    @SerialName("poll") val poll: io.telegramkt.model.poll.Poll? = null,
    /**
     * Optional. Message is a venue, information about the venue. For backward compatibility,
     * when this field is set, the location field will also be set
     */
    @SerialName("venue") val venue: Venue? = null,
    /**
     * Optional. Message is a shared location, information about the location
     */
    @SerialName("location") val location: Location? = null,
    /**
     * Optional. New members that were added to the group or supergroup and information about them
     * (the bot itself may be one of these members)
     */
    @SerialName("new_chat_members") val newChatMembers: List<User>? = null,
    /**
     * Optional. A member was removed from the group, information about them
     * (this member may be the bot itself)
     */
    @SerialName("left_chat_member") val leftChatMember: User? = null,
    /**
     * Optional. A chat title was changed to another value
     */
    @SerialName("new_chat_title") val newChatTitle: String? = null,
    /**
     * Optional. A chat photo was changed to another value
     */
    @SerialName("new_chat_photo") val newChatPhoto: List<io.telegramkt.model.photo.PhotoSize>? = null,
    /**
     * Optional. Service message: the chat photo was deleted
     */
    @SerialName("delete_chat_photo") val deleteChatPhoto: Boolean? = null,
    /**
     * Optional. Service message: the group has been created
     */
    @SerialName("group_chat_created") val groupChatCreated: Boolean? = null,
    /**
     * Optional. Service message: the supergroup has been created. This field can't be received
     * in a message coming through updates, because bot can't be a member of a suprgroup
     * when it is created
     */
    @SerialName("supergroup_chat_created") val supergroupChatCreated: Boolean? = null,
    /**
     * Optional. Service message: the channel has been created. This field can't be received
     * in a message coming through updates, because bot can't be a member of a channel
     * when it is created
     */
    @SerialName("channel_chat_created") val channelChatCreated: Boolean? = null,
    /**
     * Optional. Service message: auto-delete timer value changed in the chat
     */
    @SerialName("message_auto_delete_timer_changed") val messageAutoDeleteTimerChanged: MessageAutoDeleteTimerChanged? = null,
    /**
     * Optional. The group has been migrated to a supergroup with the specified identifier
     */
    @SerialName("migrate_to_chat_id") val migrateToChatId: Long? = null,
    /**
     * Optional. The supergroup has been migrated from a group with the specified identifier
     */
    @SerialName("migrate_from_chat_id") val migrateFromChatId: Long? = null,
    /**
     * Optional. Specified message was pinned. Note that the Message object in this field
     * will not contain further reply_to_message fields even if it itself is a reply
     */
    @SerialName("pinned_message") val pinnedMessage: MaybeInaccessibleMessage? = null,
    /**
     * Optional. Message is an invoice for a payment, information about the invoice
     */
    @SerialName("invoice") val invoice: Invoice? = null,
    /**
     * Optional. Message is a service message about a successful payment, information about the payment
     */
    @SerialName("successful_payment") val successfulPayment: SuccessfulPayment? = null,
    /**
     * Optional. Message is a service message about a refunded payment, information about the payment
     */
    @SerialName("refunded_payment") val refundedPayment: RefundedPayment? = null,
    /**
     * Optional. For a star prize in a private chat, the user that was awarded the prize
     */
    @SerialName("prize_user") val prizeUser: User? = null,
    /**
     * Optional. For a star prize in a private chat, the number of Telegram Stars in the prize
     */
    @SerialName("star_prize_amount") val starPrizeAmount: Int? = null,
    /**
     * Optional. For a star prize in a private chat, the star prize transaction partner
     */
    @SerialName("star_prize") val starPrize: TransactionPartner? = null,
    /**
     * Optional. Service message: users were shared with the bot
     */
    @SerialName("users_shared") val usersShared: UsersShared? = null,
    /**
     * Optional. Service message: a chat was shared with the bot
     */
    @SerialName("chat_shared") val chatShared: ChatShared? = null,
    /**
     * Optional. The domain name of the website on which the user has logged in
     */
    @SerialName("connected_website") val connectedWebsite: String? = null,
    /**
     * Optional. Service message: the user allowed the bot added to the attachment menu
     * to write messages
     */
    @SerialName("write_access_allowed") val writeAccessAllowed: WriteAccessAllowed? = null,
    /**
     * Optional. Telegram Passport data
     */
    @SerialName("passport_data") val passportData: PassportData? = null,
    /**
     * Optional. Service message. A user in the chat triggered another user's proximity alert
     * while sharing Live Location
     */
    @SerialName("proximity_alert_triggered") val proximityAlertTriggered: ProximityAlertTriggered? = null,
    /**
     * Optional. Service message: user boosted the chat
     */
    @SerialName("boost_added") val boostAdded: ChatBoostAdded? = null,
    /**
     * Optional. Service message: chat background set
     */
    @SerialName("chat_background_set") val chatBackgroundSet: ChatBackground? = null,
    /**
     * Optional. Service message: forum topic created
     */
    @SerialName("forum_topic_created") val forumTopicCreated: ForumTopicCreated? = null,
    /**
     * Optional. Service message: forum topic edited
     */
    @SerialName("forum_topic_edited") val forumTopicEdited: ForumTopicEdited? = null,
    /**
     * Optional. Service message: forum topic closed
     */
    @SerialName("forum_topic_closed") val forumTopicClosed: ForumTopicClosed? = null,
    /**
     * Optional. Service message: forum topic reopened
     */
    @SerialName("forum_topic_reopened") val forumTopicReopened: ForumTopicReopened? = null,
    /**
     * Optional. Service message: the 'General' forum topic hidden
     */
    @SerialName("general_forum_topic_hidden") val generalForumTopicHidden: GeneralForumTopicHidden? = null,
    /**
     * Optional. Service message: the 'General' forum topic unhidden
     */
    @SerialName("general_forum_topic_unhidden") val generalForumTopicUnhidden: GeneralForumTopicUnhidden? = null,
    /**
     * Optional. Service message: a scheduled giveaway created
     */
    @SerialName("giveaway_created") val giveawayCreated: GiveawayCreated? = null,
    /**
     * Optional. The message is a giveaway in the chat
     */
    @SerialName("giveaway") val giveaway: Giveaway? = null,
    /**
     * Optional. The message is a giveaway with the winners
     */
    @SerialName("giveaway_winners") val giveawayWinners: GiveawayWinners? = null,
    /**
     * Optional. Service message: a giveaway completed
     */
    @SerialName("giveaway_completed") val giveawayCompleted: GiveawayCompleted? = null,
    /**
     * Optional. Service message: video chat scheduled
     */
    @SerialName("video_chat_scheduled") val videoChatScheduled: VideoChatScheduled? = null,
    /**
     * Optional. Service message: video chat started
     */
    @SerialName("video_chat_started") val videoChatStarted: VideoChatStarted? = null,
    /**
     * Optional. Service message: video chat ended
     */
    @SerialName("video_chat_ended") val videoChatEnded: VideoChatEnded? = null,
    /**
     * Optional. Service message: new participants invited to a video chat
     */
    @SerialName("video_chat_participants_invited") val videoChatParticipantsInvited: VideoChatParticipantsInvited? = null,
    /**
     * Optional. Service message: data sent by a Web App
     */
    @SerialName("web_app_data") val webAppData: WebAppData? = null,
    /**
     * Optional. Inline keyboard attached to the message
     */
    @SerialName("reply_markup") val replyMarkup: InlineKeyboardMarkup? = null,
    /**
     * Optional. Service message: the chat was closed via the setChatCloseReactions setting
     */
    @SerialName("close_reactions") val closeReactions: Boolean? = null,
    /**
     * Optional. Service message: reactions turned on in the chat
     */
    @SerialName("reactions_turned_on") val reactionsTurnedOn: Boolean? = null,
    /**
     * Optional. The list of reactions added to the message
     */
    @SerialName("reactions") val reactions: List<ReactionType>? = null,
    /**
     * Optional. Service message: suggested topic created
     */
    @SerialName("suggested_topic") val suggestedTopic: SuggestedTopic? = null,
    /**
     * Optional. Service message: a gift was sent
     */
    @SerialName("gift") val gift: Gift? = null,
) {
    /**
     * Returns true if the message was edited
     */
    val isEdited: Boolean get() = editDate != null

    /**
     * Returns true if the message was forwarded
     */
    val isForwarded: Boolean get() = forwardOrigin != null

    /**
     * Returns true if the message contains text
     */
    val hasText: Boolean get() = !text.isNullOrBlank()

    /**
     * Returns true if the message contains any media (photo, video, document, etc.)
     */
    val hasMedia: Boolean get() = photo != null || video != null || animation != null ||
        audio != null || document != null || voice != null || videoNote != null || sticker != null

    /**
     * Returns true if the message is a command
     */
    val isCommand: Boolean get() = text?.startsWith("/") == true
}
