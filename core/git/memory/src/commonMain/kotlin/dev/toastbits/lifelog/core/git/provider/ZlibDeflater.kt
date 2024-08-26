package dev.toastbits.lifelog.core.git.provider

interface ZlibDeflater {
    fun deflate(input: ByteArray, output: ByteArray, writeOffset: Int = 0, inputStart: Int = 0): Int
}
