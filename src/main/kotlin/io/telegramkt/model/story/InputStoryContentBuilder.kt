package io.telegramkt.model.story

import io.telegramkt.model.file.input.InputFile

class InputStoryContentBuilder {
    private var content: InputStoryContent? = null

    fun photo(file: InputFile) {
        require(content == null) { "Content already set." }
        content = InputStoryContent.Photo(file)
    }

    fun video(
        file: InputFile,
        duration: Float? = null,
        coverFrameTimestamp: Float? = null,
        isAnimation: Boolean? = null
    ) {
        require(content == null) { "Content already set." }
        content = InputStoryContent.Video(file, duration, coverFrameTimestamp, isAnimation)
    }

    fun build() = content ?: error("Must call photo() or video().")
}