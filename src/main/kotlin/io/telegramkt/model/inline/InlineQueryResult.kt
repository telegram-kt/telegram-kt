package io.telegramkt.model.inline

import io.telegramkt.model.ParseMode
import io.telegramkt.model.keyboard.reply.InlineKeyboardMarkup
import io.telegramkt.model.message.entity.MessageEntity
import io.telegramkt.model.message.input.InputMessageContent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class InlineQueryResult {
    abstract val id: String

    @Serializable
    @SerialName("article")
    data class Article(
        @SerialName("id") override val id: String,
        @SerialName("title") val title: String,
        @SerialName("input_message_content") val inputMessageContent: InputMessageContent,
        @SerialName("reply_markup") val replyMarkup: InlineKeyboardMarkup? = null,
        @SerialName("url") val url: String? = null,
        @SerialName("hide_url") val hideUrl: Boolean? = null,
        @SerialName("description") val description: String? = null,
        @SerialName("thumbnail_url") val thumbnailUrl: String? = null,
        @SerialName("thumbnail_width") val thumbnailWidth: Int? = null,
        @SerialName("thumbnail_height") val thumbnailHeight: Int? = null,
    ) : InlineQueryResult()

    @Serializable
    @SerialName("photo")
    data class Photo(
        @SerialName("id") override val id: String,
        @SerialName("photo_url") val photoUrl: String,
        @SerialName("thumbnail_url") val thumbnailUrl: String,
        @SerialName("photo_width") val photoWidth: Int? = null,
        @SerialName("photo_height") val photoHeight: Int? = null,
        @SerialName("title") val title: String? = null,
        @SerialName("description") val description: String? = null,
        @SerialName("caption") val caption: String? = null,
        @SerialName("parse_mode") val parseMode: ParseMode? = null,
        @SerialName("caption_entities") val captionEntities: List<MessageEntity>? = null,
        @SerialName("show_caption_above_media") val showCaptionAboveMedia: Boolean? = null,
        @SerialName("reply_markup") val replyMarkup: InlineKeyboardMarkup? = null,
        @SerialName("input_message_content") val inputMessageContent: InputMessageContent? = null,
    ) : InlineQueryResult() {
        init {
           if (caption != null)  require(caption.length in 0..1024) {
               "Caption must be between 0 and 1024 characters."
           }
        }
    }

    @Serializable
    @SerialName("gif")
    data class Gif(
        @SerialName("id") override val id: String,
        @SerialName("gif_url") val gifUrl: String,
        @SerialName("gif_width") val gifWidth: Int? = null,
        @SerialName("gif_height") val gifHeight: Int? = null,
        @SerialName("gif_duration") val gifDuration: Int? = null,
        @SerialName("thumbnail_url") val thumbnailUrl: String,
        @SerialName("thumbnail_mime_type") val thumbnailMimeType: String? = null,
        @SerialName("title") val title: String? = null,
        @SerialName("caption") val caption: String? = null,
        @SerialName("parse_mode") val parseMode: ParseMode? = null,
        @SerialName("caption_entities") val captionEntities: List<MessageEntity>? = null,
        @SerialName("show_caption_above_media") val showCaptionAboveMedia: Boolean? = null,
        @SerialName("reply_markup") val replyMarkup: InlineKeyboardMarkup? = null,
        @SerialName("input_message_content") val inputMessageContent: InputMessageContent? = null,
    ) : InlineQueryResult()

    @Serializable
    @SerialName("mpeg4_gif")
    data class Mpeg4Gif(
        @SerialName("id") override val id: String,
        @SerialName("mpeg4_url") val mpeg4Url: String,
        @SerialName("mpeg4_width") val mpeg4Width: Int? = null,
        @SerialName("mpeg4_height") val mpeg4Height: Int? = null,
        @SerialName("mpeg4_duration") val mpeg4Duration: Int? = null,
        @SerialName("thumbnail_url") val thumbnailUrl: String,
        @SerialName("thumbnail_mime_type") val thumbnailMimeType: String? = null,
        @SerialName("title") val title: String? = null,
        @SerialName("caption") val caption: String? = null,
        @SerialName("parse_mode") val parseMode: ParseMode? = null,
        @SerialName("caption_entities") val captionEntities: List<MessageEntity>? = null,
        @SerialName("show_caption_above_media") val showCaptionAboveMedia: Boolean? = null,
        @SerialName("reply_markup") val replyMarkup: InlineKeyboardMarkup? = null,
        @SerialName("input_message_content") val inputMessageContent: InputMessageContent? = null,
    ) : InlineQueryResult()

    @Serializable
    @SerialName("video")
    data class Video(
        @SerialName("id") override val id: String,
        @SerialName("video_url") val videoUrl: String,
        @SerialName("mime_type") val mimeType: String,
        @SerialName("thumbnail_url") val thumbnailUrl: String,
        @SerialName("title") val title: String,
        @SerialName("caption") val caption: String? = null,
        @SerialName("parse_mode") val parseMode: ParseMode? = null,
        @SerialName("caption_entities") val captionEntities: List<MessageEntity>? = null,
        @SerialName("show_caption_above_media") val showCaptionAboveMedia: Boolean? = null,
        @SerialName("video_width") val videoWidth: Int? = null,
        @SerialName("video_height") val videoHeight: Int? = null,
        @SerialName("video_duration") val videoDuration: Int? = null,
        @SerialName("description") val description: String? = null,
        @SerialName("reply_markup") val replyMarkup: InlineKeyboardMarkup? = null,
        @SerialName("input_message_content") val inputMessageContent: InputMessageContent? = null,
    ) : InlineQueryResult()

    @Serializable
    @SerialName("audio")
    data class Audio(
        @SerialName("id") override val id: String,
        @SerialName("audio_url") val audioUrl: String,
        @SerialName("title") val title: String,
        @SerialName("caption") val caption: String? = null,
        @SerialName("parse_mode") val parseMode: ParseMode? = null,
        @SerialName("caption_entities") val captionEntities: List<MessageEntity>? = null,
        @SerialName("performer") val performer: String? = null,
        @SerialName("audio_duration") val audioDuration: Int? = null,
        @SerialName("reply_markup") val replyMarkup: InlineKeyboardMarkup? = null,
        @SerialName("input_message_content") val inputMessageContent: InputMessageContent? = null,
    ) : InlineQueryResult()

    @Serializable
    @SerialName("voice")
    data class Voice(
        @SerialName("id") override val id: String,
        @SerialName("voice_url") val voiceUrl: String,
        @SerialName("title") val title: String,
        @SerialName("caption") val caption: String? = null,
        @SerialName("parse_mode") val parseMode: ParseMode? = null,
        @SerialName("caption_entities") val captionEntities: List<MessageEntity>? = null,
        @SerialName("voice_duration") val voiceDuration: Int? = null,
        @SerialName("reply_markup") val replyMarkup: InlineKeyboardMarkup? = null,
        @SerialName("input_message_content") val inputMessageContent: InputMessageContent? = null,
    ) : InlineQueryResult()

    @Serializable
    @SerialName("document")
    data class Document(
        @SerialName("id") override val id: String,
        @SerialName("title") val title: String,
        @SerialName("caption") val caption: String? = null,
        @SerialName("parse_mode") val parseMode: ParseMode? = null,
        @SerialName("caption_entities") val captionEntities: List<MessageEntity>? = null,
        @SerialName("document_url") val documentUrl: String,
        @SerialName("mime_type") val mimeType: String,
        @SerialName("description") val description: String? = null,
        @SerialName("reply_markup") val replyMarkup: InlineKeyboardMarkup? = null,
        @SerialName("input_message_content") val inputMessageContent: InputMessageContent? = null,
        @SerialName("thumbnail_url") val thumbnailUrl: String? = null,
        @SerialName("thumbnail_width") val thumbnailWidth: Int? = null,
        @SerialName("thumbnail_height") val thumbnailHeight: Int? = null,
    ) : InlineQueryResult()

    @Serializable
    @SerialName("location")
    data class Location(
        @SerialName("id") override val id: String,
        @SerialName("latitude") val latitude: Float,
        @SerialName("longitude") val longitude: Float,
        @SerialName("title") val title: String,
        @SerialName("horizontal_accuracy") val horizontalAccuracy: Float? = null,
        @SerialName("live_period") val livePeriod: Int? = null,
        @SerialName("heading") val heading: Int? = null,
        @SerialName("proximity_alert_radius") val proximityAlertRadius: Int? = null,
        @SerialName("reply_markup") val replyMarkup: InlineKeyboardMarkup? = null,
        @SerialName("input_message_content") val inputMessageContent: InputMessageContent? = null,
        @SerialName("thumbnail_url") val thumbnailUrl: String? = null,
        @SerialName("thumbnail_width") val thumbnailWidth: Int? = null,
        @SerialName("thumbnail_height") val thumbnailHeight: Int? = null,
    ) : InlineQueryResult()

    @Serializable
    @SerialName("venue")
    data class Venue(
        @SerialName("id") override val id: String,
        @SerialName("latitude") val latitude: Float,
        @SerialName("longitude") val longitude: Float,
        @SerialName("title") val title: String,
        @SerialName("address") val address: String,
        @SerialName("foursquare_id") val foursquareId: String? = null,
        @SerialName("foursquare_type") val foursquareType: String? = null,
        @SerialName("google_place_id") val googlePlaceId: String? = null,
        @SerialName("google_place_type") val googlePlaceType: String? = null,
        @SerialName("reply_markup") val replyMarkup: InlineKeyboardMarkup? = null,
        @SerialName("input_message_content") val inputMessageContent: InputMessageContent? = null,
        @SerialName("thumbnail_url") val thumbnailUrl: String? = null,
        @SerialName("thumbnail_width") val thumbnailWidth: Int? = null,
        @SerialName("thumbnail_height") val thumbnailHeight: Int? = null,
    ) : InlineQueryResult()

    @Serializable
    @SerialName("contact")
    data class Contact(
        @SerialName("id") override val id: String,
        @SerialName("phone_number") val phoneNumber: String,
        @SerialName("first_name") val firstName: String,
        @SerialName("last_name") val lastName: String? = null,
        @SerialName("vcard") val vcard: String? = null,
        @SerialName("reply_markup") val replyMarkup: InlineKeyboardMarkup? = null,
        @SerialName("input_message_content") val inputMessageContent: InputMessageContent? = null,
        @SerialName("thumbnail_url") val thumbnailUrl: String? = null,
        @SerialName("thumbnail_width") val thumbnailWidth: Int? = null,
        @SerialName("thumbnail_height") val thumbnailHeight: Int? = null,
    ) : InlineQueryResult()

    @Serializable
    @SerialName("game")
    data class Game(
        @SerialName("id") override val id: String,
        @SerialName("game_short_name") val gameShortName: String,
        @SerialName("reply_markup") val replyMarkup: InlineKeyboardMarkup? = null,
    ) : InlineQueryResult()

    @Serializable
    @SerialName("sticker")
    data class Sticker(
        @SerialName("id") override val id: String,
        @SerialName("sticker_file_id") val stickerFileId: String,
        @SerialName("reply_markup") val replyMarkup: InlineKeyboardMarkup? = null,
        @SerialName("input_message_content") val inputMessageContent: InputMessageContent? = null,
    ) : InlineQueryResult()
}