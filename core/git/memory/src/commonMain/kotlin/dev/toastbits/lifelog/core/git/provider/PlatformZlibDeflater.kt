package dev.toastbits.lifelog.core.git.provider

expect class PlatformZlibDeflater(level: Int = 6): ZlibDeflater {
    override fun deflate(input: ByteArray, output: ByteArray, writeOffset: Int, inputStart: Int): Int
}
