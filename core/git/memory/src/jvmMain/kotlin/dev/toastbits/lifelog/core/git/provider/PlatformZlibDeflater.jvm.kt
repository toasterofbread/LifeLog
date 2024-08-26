package dev.toastbits.lifelog.core.git.provider

import java.util.zip.Deflater

actual class PlatformZlibDeflater actual constructor(private val level: Int): ZlibDeflater {
    private val deflater: Deflater = Deflater(level)

    actual override fun deflate(
        input: ByteArray,
        output: ByteArray,
        writeOffset: Int,
        inputStart: Int
    ): Int {
        deflater.reset()
        deflater.setInput(input, inputStart, input.size - inputStart)
        deflater.finish()

        val buffer: ByteArray = ByteArray(1024)
        var totalWritten: Int = 0

        while (!deflater.finished()) {
            val written: Int = deflater.deflate(buffer)
            buffer.copyInto(output, writeOffset + totalWritten, 0, written)
            totalWritten += written
        }

        return totalWritten

//        while (true) {
//            totalWritten += written
//            if (deflater.finished()) {
//                return totalWritten
//            }
//            check(written != 0) { totalWritten }
//        }
    }
}
