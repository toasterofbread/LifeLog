package dev.toastbits.lifelog.core.git.util

import dev.toastbits.lifelog.core.git.handler.GitPackFileParser

typealias ParserByteArray = GitPackFileParser.ByteArrayRegionWrapper

infix fun Byte.band(other: Long): Long =
    toLong() and other

fun ByteArray.toInt(): Int {
    var sum: Int = 0

    for (index in size - 1 downTo 0) {
        val byte: Byte = this[index]
        sum += (byte.toUByte().toLong() shl ((size - 1 - index) * Byte.SIZE_BITS)).toInt()
    }

    return sum
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

//fun ParserByteArray.toHexString(start: Int, end: Int): String = buildString {
//    for (index in start until end) {
//        append(this@toHexString[index].toHexString())
//    }
//}
