package io.telegramkt.model.media.input

import io.telegramkt.model.file.input.InputFile

class InputProfilePhotoBuilder {
    private var photo: InputProfilePhoto? = null

    fun static(file: InputFile) {
        require(photo == null) { "Already set photo" }
        photo = InputProfilePhoto.Static(file)
    }

    fun animated(file: InputFile, mainFrameTimestamp: Float? = null) {
        require(photo == null) { "Already set photo" }
        photo = InputProfilePhoto.Animated(file, mainFrameTimestamp)
    }

    internal fun build(): InputProfilePhoto =
        photo ?: error("Must call static() or animated()")
}
