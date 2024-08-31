package dev.toastbits.lifelog.core.git.memory.util

import dev.toastbits.lifelog.core.git.memory.provider.Sha1Provider
import dev.toastbits.lifelog.core.git.memory.provider.ZlibInflater

class ByteArrayRegionWrapper(private val bytes: ByteArray, private val regions: List<IntRange> = listOf(bytes.indices)) {
    val size: Int = regions.sumOf { it.size }

    fun toHexString(start: Int, end: Int): String {
        val size: Int = end - start
        val regions: List<IntRange> = regions.getRegionsStartingAt(start, size)
        return regions.joinToString("") { bytes.toHexString(it.first, it.last + 1) }
    }

    suspend fun inflate(zlibInflater: ZlibInflater, offset: Int): ZlibInflater.InflationResult {
        return zlibInflater.inflate(bytes, regions.getRegionsStartingAt(offset, null))
    }

    fun sliceArray(sliceRegion: IntRange): ByteArray {
        val (region: IntRange, regionOffset: Int) = regions.getRegionAndOffset(sliceRegion.first, sliceRegion.size)
        return bytes.sliceArray(region.first + regionOffset until region.first + regionOffset + sliceRegion.size)
    }

    fun calculateSha1Hash(sha1Provider: Sha1Provider, offset: Int = 0, length: Int = bytes.size): String {
        return sha1Provider.calculateSha1Hash(bytes, regions.getRegionsStartingAt(offset, length)).toHexString()
    }

    operator fun get(index: Int): Byte {
        val (region: IntRange, regionOffset: Int) = regions.getRegionAndOffset(index)
        return bytes[region.first + regionOffset]
    }
}
