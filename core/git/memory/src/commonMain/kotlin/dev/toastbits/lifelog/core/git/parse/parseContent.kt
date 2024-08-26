package dev.toastbits.lifelog.core.git.parse

import dev.toastbits.lifelog.core.git.model.ByteReader

internal suspend fun ByteReader.parseContent(expectedSize: Int?): Int {
    val (bytesRead: Int, bytesWritten: Int) = bytes.inflate(zlibInflater, head)

    if (expectedSize != null) {
        check(bytesWritten == expectedSize) {
            if (bytesWritten == zlibInflater.outputBytes.size)
                "Output array is not large enough (expected size: $expectedSize, array size: ${zlibInflater.outputBytes.size})"
            else
                "Expected: $expectedSize, Actual: $bytesWritten"
        }
    }

    head += bytesRead

    return bytesWritten
}
