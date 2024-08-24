package dev.toastbits.lifelog.core.git.provider

expect class PlatformZlibInflater(outputBytes: ByteArray): ZlibInflater {
    override val bytesRead: Int
    override val outputBytes: ByteArray

    override fun inflate(input: ByteArray, inputOffset: Int, inputLength: Int): Int
    override fun inflate(input: ByteArray, regions: List<IntRange>): Int

    companion object
}

fun PlatformZlibInflater.Companion.createEmpty(): PlatformZlibInflater =
    PlatformZlibInflater(byteArrayOf())
