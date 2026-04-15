package io.telegramkt.model.web

import io.telegramkt.model.LinkPreviewOptions
import io.telegramkt.model.animation.Animation
import io.telegramkt.model.audio.Audio
import io.telegramkt.model.audio.Voice
import io.telegramkt.model.chat.Chat
import io.telegramkt.model.contact.Contact
import io.telegramkt.model.document.Document
import io.telegramkt.model.game.Dice
import io.telegramkt.model.game.Game
import io.telegramkt.model.giveaway.Giveaway
import io.telegramkt.model.giveaway.GiveawayWinners
import io.telegramkt.model.location.Location
import io.telegramkt.model.location.Venue
import io.telegramkt.model.message.MessageOrigin
import io.telegramkt.model.photo.PhotoSize
import io.telegramkt.model.poll.Poll
import io.telegramkt.model.sticker.Sticker
import io.telegramkt.model.story.Story
import io.telegramkt.model.video.Video
import io.telegramkt.model.video.VideoNote
import io.telegramkt.model.payment.Invoice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Contains information about a message that is being replied to, which may come from another chat or forum topic.
 *
 * See https://core.telegram.org/bots/api#externalreplyinfo
 */
@Serializable
data class ExternalReplyInfo(
    @SerialName("origin") val origin: MessageOrigin,
    @SerialName("chat") val chat: Chat? = null,
    @SerialName("message_id") val messageId: Int? = null,
    @SerialName("link_preview_options") val linkPreviewOptions: LinkPreviewOptions? = null,
    @SerialName("animation") val animation: Animation? = null,
    @SerialName("audio") val audio: Audio? = null,
    @SerialName("document") val document: Document? = null,
    @SerialName("photo") val photo: List<PhotoSize>? = null,
    @SerialName("sticker") val sticker: Sticker? = null,
    @SerialName("story") val story: Story? = null,
    @SerialName("video") val video: Video? = null,
    @SerialName("video_note") val videoNote: VideoNote? = null,
    @SerialName("voice") val voice: Voice? = null,
    @SerialName("has_media_spoiler") val hasMediaSpoiler: Boolean? = null,
    @SerialName("contact") val contact: Contact? = null,
    @SerialName("dice") val dice: Dice? = null,
    @SerialName("game") val game: Game? = null,
    @SerialName("giveaway") val giveaway: Giveaway? = null,
    @SerialName("giveaway_winners") val giveawayWinners: GiveawayWinners? = null,
    @SerialName("invoice") val invoice: Invoice? = null,
    @SerialName("location") val location: Location? = null,
    @SerialName("poll") val poll: Poll? = null,
    @SerialName("venue") val venue: Venue? = null,
)
