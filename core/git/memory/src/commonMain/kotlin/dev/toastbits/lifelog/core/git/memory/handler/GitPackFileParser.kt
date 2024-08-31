package dev.toastbits.lifelog.core.git.memory.handler

import dev.toastbits.lifelog.core.git.memory.generate.generateGitObject
import dev.toastbits.lifelog.core.git.memory.handler.stage.GitHandlerStage
import dev.toastbits.lifelog.core.git.memory.model.ByteReader
import dev.toastbits.lifelog.core.git.memory.model.GitObject
import dev.toastbits.lifelog.core.git.memory.model.GitObjectRegistry
import dev.toastbits.lifelog.core.git.memory.model.MutableGitObjectRegistry
import dev.toastbits.lifelog.core.git.memory.parse.PackFileHeader
import dev.toastbits.lifelog.core.git.memory.parse.parseContent
import dev.toastbits.lifelog.core.git.memory.parse.parsePackFileHeader
import dev.toastbits.lifelog.core.git.memory.parse.parseRefDeltaObject
import dev.toastbits.lifelog.core.git.memory.parse.parseSizeAndTypeHeader
import dev.toastbits.lifelog.core.git.memory.provider.Sha1Provider
import dev.toastbits.lifelog.core.git.memory.provider.ZlibInflater
import dev.toastbits.lifelog.core.git.memory.util.ByteArrayRegionWrapper
import dev.toastbits.lifelog.core.git.memory.util.GitConstants
import dev.toastbits.lifelog.core.git.memory.util.ParserByteArray
import dev.toastbits.lifelog.core.git.memory.util.indexOfOrNull
import dev.toastbits.lifelog.core.git.memory.util.size
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GitPackFileParser(
    private val sha1Provider: Sha1Provider,
    private val zlibInflater: ZlibInflater,
    private val objectRegistry: MutableGitObjectRegistry,
    private val workDispatcher: CoroutineDispatcher
): MutableGitObjectRegistry by objectRegistry {
    fun interface ProgressListener {
        fun onProgress(stage: GitHandlerStage.PackFileParse, itemIndex: Int?, totalItems: Int?)
    }

    suspend fun parsePackFile(bytes: ByteArray, bytesSize: Int = bytes.size, progressListener: ProgressListener? = null) = withContext(workDispatcher) {
        progressListener?.onProgress(GitHandlerStage.PackFileParse.PREPARE_PACK, null, null)

        val packFile: ByteArrayRegionWrapper = preparePackFileBytes(bytes, bytesSize)
        val fileStart: Int = packFile.indexOfOrNull("PACK".encodeToByteArray())!!
        val reader: ByteReader = ByteReader(packFile, fileStart + 4, zlibInflater)

        progressListener?.onProgress(GitHandlerStage.PackFileParse.READ_HEADER, null, null)

        val header: PackFileHeader = reader.parsePackFileHeader()

        check(header.version == GitConstants.GIT_VERSION) { header }
        check(header.objectCount >= 0) { header }

        for (objectIndex in 0 until header.objectCount) {
            progressListener?.onProgress(GitHandlerStage.PackFileParse.PARSE_OBJECTS, objectIndex, header.objectCount)
            reader.parseAnyObject()
        }

        progressListener?.onProgress(GitHandlerStage.PackFileParse.CHECKSUM, null, null)

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
        val gitObject: GitObject = generateGitObject(
            type,
            zlibInflater.outputBytes,
            sha1Provider,
            contentRange = 0 until actualSize
        )
        writeObject(gitObject)
    }

    private suspend fun ByteReader.parseAnyObject() {
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
