package dev.toastbits.lifelog.core.git.parse

import dev.toastbits.lifelog.core.git.model.ByteReader

internal fun ByteReader.parseContent(expectedSize: Int?): Int {
    val actualSize: Int = zlibInflater.inflate(zlibInflater.outputBytes, bytes, head, bytes.size - head)

    if (expectedSize != null) {
        check(actualSize == expectedSize) { "Expected: $expectedSize, Actual: $actualSize" }
    }

    head += zlibInflater.bytesRead

    return actualSize
}
