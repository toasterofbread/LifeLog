package dev.toastbits.lifelog.core.git.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class ByteUtilsTest {
    @Test
    fun byteArrayToInt() {
        val arrays: Map<Int, ByteArray> = mapOf(
            65537 to byteArrayOf(0.toByte(), 1.toByte(), 0.toByte(), 1.toByte()),
            16843009 to byteArrayOf(1.toByte(), 1.toByte(), 1.toByte(), 1.toByte()),
            905986313 to byteArrayOf(54.toByte(), 0.toByte(), 65.toByte(), 9.toByte()),
            1222 to byteArrayOf(0.toByte(), 0.toByte(), 4.toByte(), (-58).toByte())
        )

        for ((int, bytes) in arrays) {
            assertThat(bytes.toInt()).isEqualTo(int)
        }
    }
}
