package dev.toastbits.lifelog.core.git.provider

import dev.toastbits.lifelog.core.git.util.ByteArrayAsyncInputStream
import dev.toastbits.lifelog.core.git.util.ByteArrayAsyncInputStream.Companion.ZLIB_HEADER_SIZE
import dev.toastbits.lifelog.core.git.util.ByteArrayAsyncOutputStream
import dev.toastbits.lifelog.core.git.util.adler32sum
import dev.toastbits.lifelog.core.git.util.to4ByteArrayBigEndian
import korlibs.io.compression.CompressionContext
import korlibs.io.compression.CompressionMethod
import korlibs.io.compression.deflate.Deflate

class PlatformZlibDeflater(
    private val level: Int = 0,
    private val deflater: CompressionMethod = Deflate
): ZlibDeflater {

    override suspend fun deflate(
        input: ByteArray,
        output: ByteArray,
        writeOffset: Int,
        inputStart: Int
    ): Int {
        println("a1")
        val inputStream: ByteArrayAsyncInputStream =
            ByteArrayAsyncInputStream(
                input,
                regions = listOf(inputStart until input.size)
            )

        println("a2")
        val outputStream: ByteArrayAsyncOutputStream =
            ByteArrayAsyncOutputStream(
                output,
                writeOffset = writeOffset + ZLIB_HEADER_SIZE
            )

        println("a3")
        deflater.compress(
            inputStream,
            outputStream,
            context = CompressionContext(level = level)
        )
        println("a4")

        output[writeOffset] = 0x78
        output[writeOffset + 1] = getLevelByte()
        check(ZLIB_HEADER_SIZE == 2)

        println("a5")
        val checksum: Int = input.adler32sum(inputStart until input.size)
        println("a6")
        val checksumBytes: ByteArray = checksum.to4ByteArrayBigEndian()
        println("a7")
        checksumBytes.copyInto(output, writeOffset + ZLIB_HEADER_SIZE + outputStream.bytesWritten)
        println("a8")

        return ZLIB_HEADER_SIZE + outputStream.bytesWritten + checksumBytes.size
    }

    private fun getLevelByte(): Byte =
        when (level) {
            1 -> 0x01u
            2, 3, 4, 5 -> 0x5eu
            6 -> 0x9cu
            7, 8, 9 -> 0xdau
            else -> throw IllegalStateException("Unknown compression level '$level'")
        }.toByte()
}
