package dev.toastbits.lifelog.core.git.memory.provider

import dev.toastbits.lifelog.core.git.memory.util.ByteArrayAsyncInputStream
import dev.toastbits.lifelog.core.git.memory.util.ByteArrayAsyncInputStream.Companion.ZLIB_HEADER_SIZE
import dev.toastbits.lifelog.core.git.memory.util.ByteArrayAsyncOutputStream
import dev.toastbits.lifelog.core.git.memory.util.adler32sum
import dev.toastbits.lifelog.core.git.memory.util.to4ByteArrayBigEndian
import korlibs.io.compression.CompressionContext
import korlibs.io.compression.CompressionMethod
import korlibs.io.compression.deflate.Deflate
import korlibs.io.compression.deflate.DeflatePortable

class PlatformZlibDeflater(
    private val level: Int = 6,
    private val deflater: CompressionMethod = DeflatePortable
): ZlibDeflater {

    override suspend fun deflate(
        input: ByteArray,
        output: ByteArray,
        writeOffset: Int,
        inputStart: Int
    ): Int {
        val inputStream: ByteArrayAsyncInputStream =
            ByteArrayAsyncInputStream(
                input,
                regions = listOf(inputStart until input.size)
            )

        val outputStream: ByteArrayAsyncOutputStream =
            ByteArrayAsyncOutputStream(
                output,
                writeOffset = writeOffset + ZLIB_HEADER_SIZE
            )

        deflater.compress(
            inputStream,
            outputStream,
            context = CompressionContext(level = level)
        )

        output[writeOffset] = 0x78
        output[writeOffset + 1] = getLevelByte()
        check(ZLIB_HEADER_SIZE == 2)

        val checksum: Int = input.adler32sum(inputStart until input.size)
        val checksumBytes: ByteArray = checksum.to4ByteArrayBigEndian()
        checksumBytes.copyInto(output, writeOffset + ZLIB_HEADER_SIZE + outputStream.bytesWritten)

        return ZLIB_HEADER_SIZE + outputStream.bytesWritten + checksumBytes.size
    }

    private fun getLevelByte(): Byte =
        when (level) {
            0 -> 0x0u
            1 -> 0x01u
            2, 3, 4, 5 -> 0x5eu
            6 -> 0x9cu
            7, 8, 9 -> 0xdau
            else -> throw IllegalStateException("Unknown compression level '$level'")
        }.toByte()
}
