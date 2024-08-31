package dev.toastbits.lifelog.core.git.memory.parse

import dev.toastbits.lifelog.core.git.memory.model.ByteReader
import dev.toastbits.lifelog.core.git.memory.util.toIntBigEndian

internal data class PackFileHeader(val version: Int, val objectCount: Int)

internal fun ByteReader.parsePackFileHeader(): PackFileHeader {
    val version: Int = bytes.sliceArray(head until head + 4).toIntBigEndian()
    head += 4
    val objectCount: Int = bytes.sliceArray(head until head + 4).toIntBigEndian()
    head += 4

    return PackFileHeader(version, objectCount)
}
