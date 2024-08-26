package dev.toastbits.lifelog.core.git.provider

interface ZlibInflater {
    val outputBytes: ByteArray
//    fun inflate(input: ByteArray, inputOffset: Int = 0, inputLength: Int = input.size): InflationResult
    fun inflate(input: ByteArray, regions: List<IntRange>): InflationResult

    data class InflationResult(val bytesRead: Int, val bytesWritten: Int)
}
