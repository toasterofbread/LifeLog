package dev.toastbits.lifelog.core.git.util

typealias ParserByteArray = ByteArrayRegionWrapper

infix fun Byte.band(other: Long): Long =
    toLong() and other

fun ByteArray.toIntBigEndian(): Int {
    check(size <= Int.SIZE_BYTES)

    var sum: Int = 0

    for (index in size - 1 downTo 0) {
        val byte: Byte = this[index]
        sum += byte.toUByte().toInt() shl ((size - 1 - index) * Byte.SIZE_BITS)
    }

    return sum
}

fun Int.to4ByteArrayBigEndian(output: ByteArray = ByteArray(4), writeOffset: Int = 0): ByteArray {
    for (i in 0 until 4) {
        output[i + writeOffset] = (this shr ((3 - i) * Byte.SIZE_BITS)).toByte()
    }
    return output
}

fun ByteArray.indexOfOrNull(byte: Byte, startIndex: Int = 0, endIndex: Int = size): Int? {
    for (index in startIndex until endIndex) {
        if (this[index] == byte) {
            return index
        }
    }
    return null
}

fun ParserByteArray.indexOfOrNull(subArray: ByteArray, size: Int? = null): Int? {
    var found: Int = 0
    for (index in 0 until (size ?: this.size)) {
        val byte: Byte = this[index]
        if (byte == subArray[found]) {
            if (++found == subArray.size) {
                return index - subArray.size + 1
            }
        }
        else if (found != 0) {
            found = 0
            if (byte == subArray[0]) {
                return index - subArray.size + 1
            }
        }
    }

    return null
}

val IntRange.size: Int
    get() = last - first + 1
