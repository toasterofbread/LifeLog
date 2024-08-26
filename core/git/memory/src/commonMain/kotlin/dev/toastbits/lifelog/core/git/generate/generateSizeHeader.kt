package dev.toastbits.lifelog.core.git.generate

import dev.toastbits.lifelog.core.git.model.GitObject
import dev.toastbits.lifelog.core.git.util.GitConstants
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.math.ceil
import kotlin.math.roundToInt

fun getSizeAndTypeHeaderSize(size: Int, type: GitObject.Type): Int {
    val firstByteSizeBitCount: Int = Byte.SIZE_BITS - GitConstants.OBJECT_HEADER_TYPE_BIT_COUNT - 1
    val extraByteSizeBitCount: Int = Byte.SIZE_BITS - 1
    val totalSizeBitCount: Int = Int.SIZE_BITS - size.countLeadingZeroBits()
    val extraByteCount: Int = ceil((totalSizeBitCount - firstByteSizeBitCount).toDouble() / extraByteSizeBitCount.toDouble()).roundToInt()
    return extraByteCount + 1
}

fun generateSizeAndTypeHeader(
    size: Int,
    type: GitObject.Type,
    output: ByteArray,
    writeOffset: Int = 0
): Int {
    val bytes: Int = getSizeAndTypeHeaderSize(size, type)
    val firstByteSizeBitCount: Int = Byte.SIZE_BITS - GitConstants.OBJECT_HEADER_TYPE_BIT_COUNT - 1
    val extraByteSizeBitCount: Int = Byte.SIZE_BITS - 1

    for (byte in 0 until bytes) {
        val shiftAmount: Int =
            if (byte == 0) 0
            else ((byte - 1) * extraByteSizeBitCount) + firstByteSizeBitCount

        val sizeBits: Int =
            (size shr shiftAmount).let { bits ->
                if (byte + 1 == bytes) bits and 0b01111111
                else bits or 0b10000000
            }

        output[writeOffset + byte] = sizeBits.toByte()
    }

    output[writeOffset] = (output[writeOffset] and 0b10001111u.toByte()) or (type.ordinal shl firstByteSizeBitCount).toByte()

    return bytes
}
