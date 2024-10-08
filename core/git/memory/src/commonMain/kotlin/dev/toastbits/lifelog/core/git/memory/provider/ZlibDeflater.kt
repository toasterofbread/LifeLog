package dev.toastbits.lifelog.core.git.memory.provider

interface ZlibDeflater {
    suspend fun deflate(input: ByteArray, output: ByteArray, writeOffset: Int = 0, inputStart: Int = 0): Int
}
