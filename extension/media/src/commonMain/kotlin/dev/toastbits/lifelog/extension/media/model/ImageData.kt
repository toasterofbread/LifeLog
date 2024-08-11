package dev.toastbits.lifelog.extension.media.model

class ImageData(
    val format: Format,
    val data: ByteArray
) {
    enum class Format {
        PNG
    }
}
