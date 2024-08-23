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
import dev.toastbits.lifelog.core.git.util.indexOfOrNull
import dev.toastbits.lifelog.core.git.util.size

class GitPackFileParser(
    private val sha1Provider: Sha1Provider,
    private val zlibInflater: ZlibInflater,
    private val objectRegistry: GitObjectRegistry
): GitObjectRegistry by objectRegistry {
    fun parsePackFile(bytes: ByteArray) {
        val packFile: ByteArray = preparePackFileBytes(bytes)
        val head: Int = packFile.indexOfOrNull("PACK".encodeToByteArray())!! + 4
        val reader: ByteReader = ByteReader(packFile, head, zlibInflater)

        val bytesHeader: PackFileHeader = reader.parsePackFileHeader()
        check(bytesHeader.version == 2) { bytesHeader }
        check(bytesHeader.objectCount > 0) { bytesHeader }

        for (objectIndex in 0 until bytesHeader.objectCount) {
            reader.parseAnyObject()
        }
    }

    private fun preparePackFileBytes(bytes: ByteArray): ByteArray {
        val packLines: MutableList<IntRange> = mutableListOf()
        var currentRegion: IntRange = bytes.indices

        while (currentRegion.size > 0) {
            val lineLen: Int = bytes.decodeToString(currentRegion.first, currentRegion.first + 4).toInt(16)
            if (lineLen == 0) {
                break
            }
            packLines.add(currentRegion.first + 4 until currentRegion.first + lineLen)
            currentRegion = currentRegion.first + lineLen .. currentRegion.last
        }

        return packLines.drop(1).map { (it.first + 1) .. it.last }.map { bytes.sliceArray(it) }.reduce { a, b -> a + b }
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
                TODO()
            }
            GitObject.Type.NONE,
            GitObject.Type.UNUSED -> throw IllegalStateException(objectType.name)
        }
    }
}
