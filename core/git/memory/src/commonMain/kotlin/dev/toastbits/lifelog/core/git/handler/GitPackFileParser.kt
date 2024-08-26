package dev.toastbits.lifelog.core.git.handler

import dev.toastbits.lifelog.core.git.generate.generateGitObject
import dev.toastbits.lifelog.core.git.model.ByteReader
import dev.toastbits.lifelog.core.git.model.GitObject
import dev.toastbits.lifelog.core.git.model.GitObjectRegistry
import dev.toastbits.lifelog.core.git.model.MutableGitObjectRegistry
import dev.toastbits.lifelog.core.git.parse.PackFileHeader
import dev.toastbits.lifelog.core.git.parse.parseContent
import dev.toastbits.lifelog.core.git.parse.parsePackFileHeader
import dev.toastbits.lifelog.core.git.parse.parseRefDeltaObject
import dev.toastbits.lifelog.core.git.parse.parseSizeAndTypeHeader
import dev.toastbits.lifelog.core.git.provider.Sha1Provider
import dev.toastbits.lifelog.core.git.provider.ZlibInflater
import dev.toastbits.lifelog.core.git.util.ByteArrayRegionWrapper
import dev.toastbits.lifelog.core.git.util.GitConstants
import dev.toastbits.lifelog.core.git.util.ParserByteArray
import dev.toastbits.lifelog.core.git.util.indexOfOrNull
import dev.toastbits.lifelog.core.git.util.size

class GitPackFileParser(
    private val sha1Provider: Sha1Provider,
    private val zlibInflater: ZlibInflater,
    private val objectRegistry: MutableGitObjectRegistry
): MutableGitObjectRegistry by objectRegistry {
    enum class Stage {
        PREPARE_PACK,
        READ_HEADER,
        PARSE_OBJECTS,
        CHECKSUM
    }

    fun interface ProgressListener {
        fun onProgress(stage: Stage, itemIndex: Int?, totalItems: Int?)
    }

    suspend fun parsePackFile(bytes: ByteArray, bytesSize: Int = bytes.size, progressListener: ProgressListener? = null) {
        progressListener?.onProgress(Stage.PREPARE_PACK, null, null)

        val packFile: ByteArrayRegionWrapper = preparePackFileBytes(bytes, bytesSize)
        val fileStart: Int = packFile.indexOfOrNull("PACK".encodeToByteArray())!!
        val reader: ByteReader = ByteReader(packFile, fileStart + 4, zlibInflater)

        progressListener?.onProgress(Stage.READ_HEADER, null, null)

        val header: PackFileHeader = reader.parsePackFileHeader()

        check(header.version == GitConstants.GIT_VERSION) { header }
        check(header.objectCount >= 0) { header }

        for (objectIndex in 0 until header.objectCount) {
            progressListener?.onProgress(Stage.PARSE_OBJECTS, objectIndex, header.objectCount)
            reader.parseAnyObject()
        }

        progressListener?.onProgress(Stage.CHECKSUM, null, null)

        performPackFileChecksum(reader.bytes, fileStart, reader.head)
    }

    private fun performPackFileChecksum(bytes: ParserByteArray, dataStart: Int, dataEnd: Int) {
        val hash: String = bytes.calculateSha1Hash(sha1Provider, dataStart, dataEnd - dataStart)
        val checksum: String = bytes.toHexString(dataEnd, dataEnd + Sha1Provider.SHA1_BYTES)

        check(hash == checksum) { "SHA1 hash of pack file content ($hash) does not match received checksum ($checksum)" }
    }

    private fun preparePackFileBytes(bytes: ByteArray, bytesSize: Int): ParserByteArray {
        if (bytes.decodeToString(0, 4) == "PACK") {
            return ByteArrayRegionWrapper(bytes, listOf(0 until bytesSize))
        }

        val packLines: MutableList<IntRange> = mutableListOf()
        var currentRegion: IntRange = bytes.indices

        while (currentRegion.size > 0) {
            val lineLen: Int = bytes.decodeToString(currentRegion.first, currentRegion.first + 4).toInt(16)
            if (lineLen == 0) {
                break
            }
            if (lineLen >= 5) {
                packLines.add(currentRegion.first + 5 until currentRegion.first + lineLen)
            }
            currentRegion = currentRegion.first + lineLen .. currentRegion.last
        }

        return ByteArrayRegionWrapper(bytes, packLines.drop(1))
    }

    private suspend fun ByteReader.parseRawContentObject(type: GitObject.Type, expectedContentSize: Int) {
        val actualSize: Int = parseContent(expectedContentSize)
        val gitObject: GitObject = generateGitObject(type, zlibInflater.outputBytes, sha1Provider, contentRange = 0 until actualSize)
        writeObject(gitObject)
    }

    private suspend fun ByteReader.parseAnyObject() {
        val (objectContentSize: Int, objectType: GitObject.Type) = parseSizeAndTypeHeader()
        println("obj $objectContentSize $objectType")

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
