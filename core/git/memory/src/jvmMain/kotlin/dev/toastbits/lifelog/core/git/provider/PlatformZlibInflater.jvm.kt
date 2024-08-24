package dev.toastbits.lifelog.core.git.provider

import com.jcraft.jzlib.Inflater
import com.jcraft.jzlib.JZlib
import dev.toastbits.lifelog.core.git.util.size

actual class PlatformZlibInflater actual constructor(
    actual override val outputBytes: ByteArray
): ZlibInflater {
    private val inflater: Inflater = Inflater()

    actual override val bytesRead: Int
        get() = inflater.totalIn.toInt()

    actual override fun inflate(
        input: ByteArray,
        inputOffset: Int,
        inputLength: Int
    ): Int {
        inflater.init()
        inflater.setInput(input, inputOffset, inputLength, false)
        inflater.setOutput(outputBytes)
        inflater.inflate(JZlib.Z_NO_FLUSH)
        return inflater.totalOut.toInt()
    }

    actual override fun inflate(
        input: ByteArray,
        regions: List<IntRange>
    ): Int {
        inflater.init()
        inflater.setOutput(outputBytes)

        for (region in regions) {
            inflater.setInput(input, region.first, region.size, false)
            val result: Int = inflater.inflate(JZlib.Z_NO_FLUSH)
            when (result) {
                JZlib.Z_OK -> {}
                JZlib.Z_STREAM_END -> break
                else -> throw RuntimeException("Inflation failed ($result)")
            }
        }

        return inflater.totalOut.toInt()
    }

    actual companion object
}
