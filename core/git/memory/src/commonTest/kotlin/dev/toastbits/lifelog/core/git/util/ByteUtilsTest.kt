package dev.toastbits.lifelog.core.git.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import kotlin.test.Test

class ByteUtilsTest {
    private val intByteArrays: Map<Int, ByteArray> =
        mapOf(
            65537 to byteArrayOf(0.toByte(), 1.toByte(), 0.toByte(), 1.toByte()),
            16843009 to byteArrayOf(1.toByte(), 1.toByte(), 1.toByte(), 1.toByte()),
            905986313 to byteArrayOf(54.toByte(), 0.toByte(), 65.toByte(), 9.toByte()),
            1222 to byteArrayOf(0.toByte(), 0.toByte(), 4.toByte(), (-58).toByte())
        )

    @Test
    fun byteArrayToInt() {
        for ((int, bytes) in intByteArrays) {
            assertThat(bytes.toIntBigEndian()).isEqualTo(int)
        }
    }

    @Test
    fun intToByteArray() {
        for ((int, bytes) in intByteArrays) {
            assertThat(int.to4ByteArrayBigEndian().contentEquals(bytes)).isTrue()
        }
    }

    @Test
    fun intToOurByteArray() {
        for (extraBytes in 0 .. 5) {
            val array: ByteArray = ByteArray(4 + extraBytes)
            for ((int, bytes) in intByteArrays) {
                int.to4ByteArrayBigEndian(array, extraBytes)
                assertThat(array.sliceArray(extraBytes until array.size).contentEquals(bytes)).isTrue()
            }
        }
    }
}
