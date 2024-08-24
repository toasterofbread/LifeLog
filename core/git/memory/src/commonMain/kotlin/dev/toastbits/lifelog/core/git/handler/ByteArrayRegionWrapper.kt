package dev.toastbits.lifelog.core.git.handler

import dev.toastbits.lifelog.core.git.provider.Sha1Provider
import dev.toastbits.lifelog.core.git.provider.ZlibInflater
import dev.toastbits.lifelog.core.git.util.size

class ByteArrayRegionWrapper(private val bytes: ByteArray, private val regions: List<IntRange> = listOf(bytes.indices)) {
    val size: Int = regions.sumOf { it.size }

    private fun getRegionAndOffset(index: Int, sizeCheck: Int? = null): Triple<IntRange, Int, Int> {
        var currentIndex: Int = index
        for ((regionIndex, region) in regions.withIndex()) {
            if (currentIndex < region.size) {
                if (sizeCheck != null) {
                    check(region.first + currentIndex + sizeCheck <= region.last) { "$index $sizeCheck $region $currentIndex" }
                }

                return Triple(region, currentIndex, regionIndex)
            }
            currentIndex -= region.size
        }
        throw IndexOutOfBoundsException("Index: $index Size: $size")
    }

    private fun getRegionsStartingAt(index: Int, length: Int?): List<IntRange> {
        val (firstRegion: IntRange, regionOffset: Int, regionIndex: Int) = getRegionAndOffset(index)
        if (length == null) {
            return listOf(firstRegion.first + regionOffset .. firstRegion.last) + regions.drop(regionIndex + 1)
        }

        if (length <= firstRegion.size - regionOffset) {
            return listOf(firstRegion.first + regionOffset until firstRegion.first + regionOffset + length)
        }

        val ret: MutableList<IntRange> = mutableListOf(firstRegion.first + regionOffset .. firstRegion.last)
        var remainingLength: Int = length - firstRegion.size + regionOffset

        for (nextRegionIndex in regionIndex + 1 until regions.size) {
            val region: IntRange = regions[nextRegionIndex]
            if (remainingLength < region.size) {
                ret.add(region.first until region.first + remainingLength)
                check(ret.all { a -> regions.any { b -> b.contains(a.first) && b.contains(a.last) } })
                break
            }
            ret.add(region)
            remainingLength -= region.size
        }

        return ret
    }

    fun toHexString(start: Int, end: Int): String {
        val size: Int = end - start
        val regions: List<IntRange> = getRegionsStartingAt(start, size)
        return regions.joinToString("") { bytes.toHexString(it.first, it.last + 1) }
    }

    fun inflate(zlibInflater: ZlibInflater, offset: Int): Int {
        return zlibInflater.inflate(bytes, getRegionsStartingAt(offset, null))
    }

    fun sliceArray(sliceRegion: IntRange): ByteArray {
        val (region: IntRange, regionOffset: Int) = getRegionAndOffset(sliceRegion.first, sliceRegion.size)
        return bytes.sliceArray(region.first + regionOffset until region.first + regionOffset + sliceRegion.size)
    }

    fun calculateSha1Hash(sha1Provider: Sha1Provider, offset: Int = 0, length: Int = bytes.size): String {
        return sha1Provider.calculateSha1Hash(bytes, getRegionsStartingAt(offset, length))
    }

    operator fun get(index: Int): Byte {
        val (region: IntRange, regionOffset: Int) = getRegionAndOffset(index)
        return bytes[region.first + regionOffset]
    }
}
