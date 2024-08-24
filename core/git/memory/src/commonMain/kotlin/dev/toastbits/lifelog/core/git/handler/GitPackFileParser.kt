package dev.toastbits.lifelog.core.git.handler

import dev.toastbits.lifelog.core.git.model.ByteReader
import dev.toastbits.lifelog.core.git.model.GitObject
import dev.toastbits.lifelog.core.git.model.GitObjectRegistry
import dev.toastbits.lifelog.core.git.parse.PackFileHeader
import dev.toastbits.lifelog.core.git.parse.parseContent
import dev.toastbits.lifelog.core.git.parse.parsePackFileHeader
import dev.toastbits.lifelog.core.git.parse.parseRefDeltaObject
import dev.toastbits.lifelog.core.git.parse.parseSizeAndTypeHeader
import dev.toastbits.lifelog.core.git.provider.Sha1Provider
import dev.toastbits.lifelog.core.git.provider.ZlibInflater
import dev.toastbits.lifelog.core.git.util.ParserByteArray
import dev.toastbits.lifelog.core.git.util.indexOfOrNull
import dev.toastbits.lifelog.core.git.util.size

class GitPackFileParser(
    private val sha1Provider: Sha1Provider,
    private val zlibInflater: ZlibInflater,
    private val objectRegistry: GitObjectRegistry
): GitObjectRegistry by objectRegistry {
    enum class Stage {
        PREPARE_PACK,
        READ_HEADER,
        PARSE_OBJECTS,
        CHECKSUM
    }

    fun interface ProgressListener {
        fun onProgress(stage: Stage, itemIndex: Int?, totalItems: Int?)
    }

    fun parsePackFile(bytes: ByteArray, progressListener: ProgressListener? = null) {
        progressListener?.onProgress(Stage.PREPARE_PACK, null, null)

        val packFile: ByteArrayRegionWrapper = preparePackFileBytes(bytes)
        val fileStart: Int = packFile.indexOfOrNull("PACK".encodeToByteArray())!!
        val reader: ByteReader = ByteReader(packFile, fileStart + 4, zlibInflater)

        progressListener?.onProgress(Stage.READ_HEADER, null, null)

        val header: PackFileHeader = reader.parsePackFileHeader()

        check(header.version == 2) { header }
        check(header.objectCount > 0) { header }

        for (objectIndex in 0 until header.objectCount) {
            progressListener?.onProgress(Stage.PARSE_OBJECTS, objectIndex, header.objectCount)
            reader.parseAnyObject()
        }

        progressListener?.onProgress(Stage.CHECKSUM, null, null)

        performPackFileChecksum(reader.bytes, fileStart, reader.head)
    }

    private fun performPackFileChecksum(bytes: ParserByteArray, dataStart: Int, dataEnd: Int) {
        val hash: String = bytes.calculateSha1Hash(sha1Provider, dataStart, dataEnd - dataStart)
        val checksum: String = bytes.toHexString(dataEnd, dataEnd + 20)

        check(hash == checksum) { "SHA1 hash of pack file content ($hash) does not match received checksum ($checksum)" }
    }

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

    private fun preparePackFileBytes(bytes: ByteArray): ParserByteArray {
        val packLines: MutableList<IntRange> = mutableListOf()
        var currentRegion: IntRange = bytes.indices

        while (currentRegion.size > 0) {
            val lineLen: Int = bytes.decodeToString(currentRegion.first, currentRegion.first + 4).toInt(16)
            if (lineLen == 0) {
                break
            }
            if (lineLen >= 4) {
                packLines.add(currentRegion.first + 4 until currentRegion.first + lineLen)
            }
            currentRegion = currentRegion.first + lineLen .. currentRegion.last
        }

        return ByteArrayRegionWrapper(bytes, packLines.drop(1).map { it.first + 1 .. it.last })
    }

    private fun ByteReader.parseRawContentObject(type: GitObject.Type, expectedContentSize: Int) {
        val actualSize: Int = parseContent(expectedContentSize)
        val gitObject: GitObject = GitObject.create(type, zlibInflater.outputBytes, actualSize, sha1Provider)
        writeObject(gitObject)
    }

    private fun ByteReader.parseAnyObject() {
        val (objectContentSize: Int, objectType: GitObject.Type) = parseSizeAndTypeHeader()

        when (objectType) {
            GitObject.Type.COMMIT,
            GitObject.Type.TREE,
            GitObject.Type.TAG,
            GitObject.Type.BLOB -> {
                parseRawContentObject(objectType, objectContentSize)
            }
            GitObject.Type.REF_DELTA -> {
                parseRefDeltaObject(sha1Provider, objectRegistry, zlibInflater)
            }
            GitObject.Type.OFS_DELTA -> {
                throw UnsupportedOperationException()
            }
            GitObject.Type.NONE,
            GitObject.Type.UNUSED -> throw IllegalStateException(objectType.name)
        }
    }
}
