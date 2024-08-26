package dev.toastbits.lifelog.core.git.generator

import assertk.assertThat
import assertk.assertions.isNotZero
import assertk.assertions.isTrue
import assertk.assertions.isZero
import dev.toastbits.lifelog.core.git.generate.generateSizeAndTypeHeader
import dev.toastbits.lifelog.core.git.generate.getSizeAndTypeHeaderSize
import dev.toastbits.lifelog.core.git.model.GitObject
import dev.toastbits.lifelog.core.git.util.GitConstants
import kotlinx.coroutines.test.runTest
import kotlin.math.pow
import kotlin.test.Test

@OptIn(ExperimentalUnsignedTypes::class)
class GenerateSizeHeaderTest {
    @Test
    fun testGenerateSizeAndTypeHeader() = runTest {
        val values: Map<Int, ByteArray> =
            mapOf(
                0 to byteArrayOf(0b0),
                1 to byteArrayOf(0b00000001),
                0b111100101010101 to ubyteArrayOf(0b10000101u, 0b10010101u, 0b00001111u).toByteArray()
            )

        check(GitConstants.OBJECT_HEADER_TYPE_BIT_COUNT == 3)

        for (type in GitObject.Type.entries) {
            check(type.ordinal <= 2.0.pow(GitConstants.OBJECT_HEADER_TYPE_BIT_COUNT))

            for ((size, bytes) in values) {
                val typedBytes: ByteArray =
                    bytes.toMutableList().apply {
                        val shiftAmount: Int = Byte.SIZE_BITS - 1 - GitConstants.OBJECT_HEADER_TYPE_BIT_COUNT
                        this[0] = ((first().toInt() and 0b10001111) or (type.ordinal shl shiftAmount)).toByte()
                    }.toByteArray()

                val outBytes: ByteArray = ByteArray(getSizeAndTypeHeaderSize(size, type))
                generateSizeAndTypeHeader(size, type, outBytes)
                assertThat(outBytes.contentEquals(typedBytes)).isTrue()

                for (i in 0 until outBytes.size - 1) {
                    assertThat(outBytes[i].toInt() and 0b10000000).isNotZero()
                }

                assertThat(outBytes.last().toInt() and 0b10000000).isZero()
            }
        }
    }
}
