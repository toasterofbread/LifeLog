package dev.toastbits.lifelog.core.git.provider

interface ZlibInflater {
    val outputBytes: ByteArray
    suspend fun inflate(input: ByteArray, regions: List<IntRange>): InflationResult

    data class InflationResult(val bytesRead: Int, val bytesWritten: Int)
}
