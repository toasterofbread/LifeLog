package dev.toastbits.lifelog.core.git.provider

interface ZlibInflater {
    val outputBytes: ByteArray
    val bytesRead: Int
    fun inflate(input: ByteArray, inputOffset: Int = 0, inputLength: Int = input.size): Int
    fun inflate(input: ByteArray, regions: List<IntRange>): Int
}
