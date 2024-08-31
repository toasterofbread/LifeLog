package dev.toastbits.lifelog.core.git.memory.provider

import dev.toastbits.lifelog.core.git.memory.util.ByteArrayAsyncInputStream
import dev.toastbits.lifelog.core.git.memory.util.ByteArrayAsyncInputStream.Companion.ZLIB_HEADER_SIZE
import dev.toastbits.lifelog.core.git.memory.util.ByteArrayAsyncOutputStream
import dev.toastbits.lifelog.core.git.memory.util.adler32sum
import dev.toastbits.lifelog.core.git.memory.util.getRegionsStartingAt
import dev.toastbits.lifelog.core.git.memory.util.to4ByteArrayBigEndian
import korlibs.io.compression.CompressionMethod
import korlibs.io.compression.deflate.Deflate
import korlibs.io.compression.deflate.DeflatePortable
import korlibs.io.stream.AsyncInputStream
import korlibs.io.stream.AsyncOutputStream
import kotlin.math.exp

class PlatformZlibInflater(
    override val outputBytes: ByteArray,
    private val inflater: CompressionMethod = DeflatePortable
): ZlibInflater {
    private val outputStream: ByteArrayAsyncOutputStream = ByteArrayAsyncOutputStream(outputBytes)

    override suspend fun inflate(
        input: ByteArray,
        regions: List<IntRange>
    ): ZlibInflater.InflationResult {
        val bytesRead: Int = performInflate(input, regions, inflater)

        val hash: ByteArray = outputBytes.adler32sum(0 until outputStream.bytesWritten).to4ByteArrayBigEndian()
        val hashByteIndices: List<Int> = regions.getRegionsStartingAt(bytesRead, hash.size).flatMap { it.toList() }

        val checksum: ByteArray = ByteArray(hash.size) { input[hashByteIndices[it]] }
        check(hash.contentEquals(checksum)) { "${hash.toHexString()} ${checksum.toHexString()}" }

        return ZlibInflater.InflationResult(bytesRead + hash.size, outputStream.bytesWritten)
    }

    private suspend fun performInflate(input: ByteArray, regions: List<IntRange>, method: CompressionMethod): Int {
        require(regions.isNotEmpty())
        outputStream.reset()

        val inputStream: ByteArrayAsyncInputStream =
            ByteArrayAsyncInputStream(
                input,
                regions,
                skipBytes = ZLIB_HEADER_SIZE
            )

        return ZLIB_HEADER_SIZE + method.uncompress(inputStream, outputStream).toInt()
    }

    companion object {
        fun createEmpty(): PlatformZlibInflater =
            PlatformZlibInflater(byteArrayOf())
    }
}
