package dev.toastbits.lifelog.core.git.provider

import java.util.zip.Inflater

actual class PlatformZlibInflater actual constructor(
    actual override val outputBytes: ByteArray
): ZlibInflater {
    private val inflater: Inflater = Inflater()

    actual override val bytesRead: Int
        get() = inflater.bytesRead.toInt()

    actual override fun inflate(
        output: ByteArray,
        input: ByteArray,
        inputOffset: Int,
        inputLength: Int
    ): Int {
        inflater.reset()
        inflater.setInput(input, inputOffset, inputLength)
        return inflater.inflate(output)
    }

    actual companion object
}
