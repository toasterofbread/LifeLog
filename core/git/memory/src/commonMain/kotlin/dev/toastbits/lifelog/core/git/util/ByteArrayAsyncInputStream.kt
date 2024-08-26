package dev.toastbits.lifelog.core.git.util

import korlibs.io.stream.AsyncGetLengthStream
import korlibs.io.stream.AsyncInputStream

internal class ByteArrayAsyncInputStream(
    private val input: ByteArray,
    private val regions: List<IntRange> = listOf(input.indices),
    skipBytes: Int = 0
): AsyncInputStream, AsyncGetLengthStream {
    private var closed: Boolean = false
    private var bytesRead: Int = skipBytes

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

    override suspend fun getLength(): Long = regions.sumOf { it.size }.toLong()

    override suspend fun hasLength(): Boolean = true

    companion object {
        const val ZLIB_HEADER_SIZE: Int = 2
    }
}
