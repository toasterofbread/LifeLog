package dev.toastbits.lifelog.core.git.parse

import dev.toastbits.lifelog.core.git.model.ByteReader
import dev.toastbits.lifelog.core.git.model.GitObject
import dev.toastbits.lifelog.core.git.util.band

internal data class SizeAndTypeHeader(val size: Int, val type: GitObject.Type)

// Write the inverse function of parseSizeHeader, which should output a ByteArray based on the input size

private fun ByteReader.parseNextFullSize(part: Int): Pair<Boolean, Long> {
    val byte: Byte = bytes[head++]
    val readMore: Boolean = (byte band 0b10000000) != 0L
    val size: Long = (byte band 0b01111111) shl (4 + ((part - 1) * 7))
    return readMore to size
}

internal fun ByteReader.parseSizeHeader(partOffset: Int = 0): Int {
    var part: Int = partOffset
    var size: Long = 0

    while (true) {
        val (readMore: Boolean, nextSize: Long) = parseNextFullSize(part++)
        size += nextSize

        if (!readMore) {
            break
        }
    }

    return size.toInt()
}

internal fun ByteReader.parseSizeAndTypeHeader(): SizeAndTypeHeader {
    val byte: Byte = bytes[head++]

    val objTypeIndex: Long = (byte band 0b01110000) shr 4
    val readMore: Boolean = (byte band 0b10000000) != 0L
    var size: Long = byte band 0b00001111

    if (readMore) {
        size += parseSizeHeader(1)
    }

    val objectType: GitObject.Type =
        try {
            GitObject.Type.entries[objTypeIndex.toInt()]
        }
        catch (e: Throwable) {
            throw RuntimeException("objTypeIndex=$objTypeIndex", e)
        }

    return SizeAndTypeHeader(size.toInt(), objectType)
}
