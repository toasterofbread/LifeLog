package dev.toastbits.lifelog.core.git.provider

expect class PlatformZlibInflater(outputBytes: ByteArray): ZlibInflater {
    override val bytesRead: Int
    override val outputBytes: ByteArray
    override fun inflate(
        output: ByteArray,
        input: ByteArray,
        inputOffset: Int,
        inputLength: Int
    ): Int

    companion object
}

fun PlatformZlibInflater.Companion.createEmpty(): PlatformZlibInflater =
    PlatformZlibInflater(byteArrayOf())
