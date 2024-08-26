package dev.toastbits.lifelog.core.git.provider

import dev.toastbits.lifelog.core.git.util.ByteArrayAsyncInputStream
import dev.toastbits.lifelog.core.git.util.ByteArrayAsyncInputStream.Companion.ZLIB_HEADER_SIZE
import dev.toastbits.lifelog.core.git.util.ByteArrayAsyncOutputStream
import dev.toastbits.lifelog.core.git.util.adler32sum
import dev.toastbits.lifelog.core.git.util.getRegionsStartingAt
import dev.toastbits.lifelog.core.git.util.to4ByteArrayBigEndian
import korlibs.io.compression.CompressionMethod
import korlibs.io.compression.deflate.Deflate
import korlibs.io.compression.deflate.DeflatePortable

class PlatformZlibInflater(
    override val outputBytes: ByteArray,
    private val inflater: CompressionMethod = DeflatePortable
): ZlibInflater {
    private val outputStream: ByteArrayAsyncOutputStream = ByteArrayAsyncOutputStream(outputBytes)

    override suspend fun inflate(
        input: ByteArray,
        regions: List<IntRange>
    ): ZlibInflater.InflationResult {
        require(regions.isNotEmpty())
        outputStream.reset()

        val inputStream: ByteArrayAsyncInputStream =
            ByteArrayAsyncInputStream(
                input,
                regions,
                skipBytes = ZLIB_HEADER_SIZE
            )

        val bytesRead: Int = ZLIB_HEADER_SIZE + inflater.uncompress(inputStream, outputStream).toInt()

        val checksum: ByteArray = outputBytes.adler32sum(0 until outputStream.bytesWritten).to4ByteArrayBigEndian()
        val hashByteIndices: List<Int> = regions.getRegionsStartingAt(bytesRead, checksum.size).flatMap { it.toList() }

        val hashBytes: ByteArray = ByteArray(checksum.size) { input[hashByteIndices[it]] }
        check(hashBytes.contentEquals(checksum))

        return ZlibInflater.InflationResult(bytesRead + checksum.size, outputStream.bytesWritten)
    }

    companion object {
        fun createEmpty(): PlatformZlibInflater =
            PlatformZlibInflater(byteArrayOf())
    }
}
