package dev.toastbits.lifelog.core.git.parse

import dev.toastbits.lifelog.core.git.model.ByteReader

internal fun ByteReader.parseContent(expectedSize: Int?): Int {
    val actualSize: Int = bytes.inflate(zlibInflater, head)

    if (expectedSize != null) {
        check(actualSize == expectedSize) {
            if (actualSize == zlibInflater.outputBytes.size)
                "Output array is not large enough (expected size: $expectedSize, array size: ${zlibInflater.outputBytes.size})"
            else
                "Expected: $expectedSize, Actual: $actualSize"
        }
    }

    head += zlibInflater.bytesRead

    return actualSize
}
