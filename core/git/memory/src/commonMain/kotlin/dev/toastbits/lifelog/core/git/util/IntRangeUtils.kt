package dev.toastbits.lifelog.core.git.util

val IntRange.size: Int
    get() = last - first + 1

fun List<IntRange>.getRegionsStartingAt(index: Int, length: Int?): List<IntRange> {
    val (firstRegion: IntRange, regionOffset: Int, regionIndex: Int) = getRegionAndOffset(index)
    if (length == null) {
        return listOf(firstRegion.first + regionOffset .. firstRegion.last) + drop(regionIndex + 1)
    }

    if (length <= firstRegion.size - regionOffset) {
        return listOf(firstRegion.first + regionOffset until firstRegion.first + regionOffset + length)
    }

    val ret: MutableList<IntRange> = mutableListOf(firstRegion.first + regionOffset .. firstRegion.last)
    var remainingLength: Int = length - firstRegion.size + regionOffset

    for (nextRegionIndex in regionIndex + 1 until size) {
        val region: IntRange = this[nextRegionIndex]
        if (remainingLength < region.size) {
            ret.add(region.first until region.first + remainingLength)
            check(ret.all { a -> this.any { b -> b.contains(a.first) && b.contains(a.last) } })
            break
        }
        ret.add(region)
        remainingLength -= region.size
    }

    return ret
}

fun List<IntRange>.getRegionAndOffset(index: Int, sizeCheck: Int? = null): Triple<IntRange, Int, Int> {
    var currentIndex: Int = index
    for ((regionIndex, region) in this.withIndex()) {
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
