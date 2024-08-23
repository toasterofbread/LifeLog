package dev.toastbits.lifelog.core.git.provider

interface ZlibInflater {
    val outputBytes: ByteArray
    val bytesRead: Int
    fun inflate(output: ByteArray, input: ByteArray, inputOffset: Int = 0, inputLength: Int = input.size): Int
}
