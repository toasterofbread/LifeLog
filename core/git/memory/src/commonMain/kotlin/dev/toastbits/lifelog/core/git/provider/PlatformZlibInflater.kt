package dev.toastbits.lifelog.core.git.provider

import dev.toastbits.lifelog.core.git.util.getRegionsStartingAt
import dev.toastbits.lifelog.core.git.util.size
import korlibs.io.async.runBlockingNoJs
import korlibs.io.compression.CompressionMethod
import korlibs.io.compression.deflate.Deflate
import korlibs.io.stream.AsyncInputStream
import korlibs.io.stream.AsyncOutputStream

class PlatformZlibInflater(
    override val outputBytes: ByteArray,
    private val inflater: CompressionMethod = Deflate
): ZlibInflater {
    private val outputStream: ByteArrayDeflaterAsyncOutputStream = ByteArrayDeflaterAsyncOutputStream(outputBytes)

    override fun inflate(
        input: ByteArray,
        regions: List<IntRange>
    ): ZlibInflater.InflationResult = runBlockingNoJs {
        outputStream.reset()

        val inputStream: RegionalByteArrayAsyncInputStream =
            RegionalByteArrayAsyncInputStream(
                input,
                regions,
                skipBytes = ZLIB_HEADER_SIZE
            )

        val bytesRead: Int = inflater.uncompress(inputStream, outputStream).toInt() + EXTRA_READ_BYTES
        return@runBlockingNoJs ZlibInflater.InflationResult(bytesRead, outputStream.bytesWritten)
    }

    companion object {
        private const val ZLIB_HEADER_SIZE: Int = 2

        // I'm not sure what defines this but it appears to be consistent
        private const val EXTRA_READ_BYTES: Int = 6
    }
}

private class RegionalByteArrayAsyncInputStream(
    private val input: ByteArray,
    private val regions: List<IntRange>,
    skipBytes: Int = 0
): AsyncInputStream {
    private var closed: Boolean = false

    var bytesRead: Int = skipBytes

    override suspend fun read(buffer: ByteArray, offset: Int, len: Int): Int {
        check(!closed)

        val readRegions: List<IntRange> =
            try {
                regions.getRegionsStartingAt(bytesRead, len)
            }
            catch (_: Throwable) {
                return 0
            }

        var written: Int = 0
        for (region in readRegions) {
            input.copyInto(buffer, offset + written, region.first, region.last + 1)
            written += region.size
            bytesRead += region.size
        }

        return written
    }

    override suspend fun close() {
        closed = true
    }
}

private class ByteArrayDeflaterAsyncOutputStream(private val output: ByteArray): AsyncOutputStream {
    private var closed: Boolean = false

    var bytesWritten: Int = 0
        private set

    override suspend fun close() {
        check(!closed)
    }

    override suspend fun write(buffer: ByteArray, offset: Int, len: Int) {
        closed = true

        buffer.copyInto(output, bytesWritten, startIndex = offset, endIndex = offset + len)
        bytesWritten += len
    }

    fun reset() {
        bytesWritten = 0
    }
}

fun PlatformZlibInflater.Companion.createEmpty(): PlatformZlibInflater =
    PlatformZlibInflater(byteArrayOf())
