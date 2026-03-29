package io.telegramkt.model.media.input

import io.telegramkt.model.file.input.InputFile
import io.telegramkt.model.media.input.builder.InputMediaAudioBuilder
import io.telegramkt.model.media.input.builder.InputMediaDocumentBuilder
import io.telegramkt.model.media.input.builder.InputMediaPhotoBuilder
import io.telegramkt.model.media.input.builder.InputMediaVideoBuilder

sealed class MediaGroupBuilder {
    abstract val media: List<AlbumableMedia>

    class PhotoVideo : MediaGroupBuilder() {
        private val _media = mutableListOf<AlbumableMedia>()
        override val media: List<AlbumableMedia> get() = _media.toList()

        fun photo(file: InputFile, block: InputMediaPhotoBuilder.() -> Unit = {}): PhotoVideo {
            _media.add(InputMediaPhotoBuilder(file).apply(block).build())
            return this
        }

        fun video(file: InputFile, block: InputMediaVideoBuilder.() -> Unit = {}): PhotoVideo {
            _media.add(InputMediaVideoBuilder(file).apply(block).build())
            return this
        }
    }

    class Documents : MediaGroupBuilder() {
        private val _media = mutableListOf<AlbumableMedia>()
        override val media: List<AlbumableMedia> get() = _media.toList()

        fun document(file: InputFile, block: InputMediaDocumentBuilder.() -> Unit = {}): Documents {
            _media.add(InputMediaDocumentBuilder(file).apply(block).build())
            return this
        }
    }

    class Audio : MediaGroupBuilder() {
        private val _media = mutableListOf<AlbumableMedia>()
        override val media: List<AlbumableMedia> get() = _media.toList()

        fun audio(file: InputFile, block: InputMediaAudioBuilder.() -> Unit = {}): Audio {
            _media.add(InputMediaAudioBuilder(file).apply(block).build())
            return this
        }
    }

    companion object {
        fun photoVideo() = PhotoVideo()
        fun documents() = Documents()
        fun audio() = Audio()
    }
}