package dev.toastbits.lifelog.core.git.util

import korlibs.io.stream.AsyncOutputStream

internal class ByteArrayAsyncOutputStream(
    private val output: ByteArray,
    private val writeOffset: Int = 0
): AsyncOutputStream {
    private var closed: Boolean = false

    var bytesWritten: Int = 0
        private set

    override suspend fun write(buffer: ByteArray, offset: Int, len: Int) {
        check(!closed)

        buffer.copyInto(output, bytesWritten + writeOffset, startIndex = offset, endIndex = offset + len)
        bytesWritten += len
    }

    override suspend fun close() {
        closed = true
    }

    fun reset() {
        bytesWritten = 0
    }
}
