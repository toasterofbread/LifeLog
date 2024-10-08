package dev.toastbits.lifelog.core.git.memory.handler

import dev.toastbits.lifelog.core.git.memory.generate.generateSizeAndTypeHeader
import dev.toastbits.lifelog.core.git.memory.generate.getSizeAndTypeHeaderSize
import dev.toastbits.lifelog.core.git.memory.model.GitObject
import dev.toastbits.lifelog.core.git.memory.provider.Sha1Provider
import dev.toastbits.lifelog.core.git.memory.provider.ZlibDeflater
import dev.toastbits.lifelog.core.git.memory.util.GitConstants
import dev.toastbits.lifelog.core.git.memory.util.to4ByteArrayBigEndian

class GitPackFileGenerator(
    private val sha1Provider: Sha1Provider,
    private val deflater: ZlibDeflater
) {
    data class PackFile(val bytes: ByteArray, val size: Int)

    fun interface ProgressListener {
        fun onProgress(objectIndex: Int, totalObjects: Int)
    }

    suspend fun generatePackFile(
        objects: Collection<GitObject>,
        progressListener: ProgressListener? = null
    ): PackFile {
        val maximumTotalSize: Int = (
            HEADER_SIZE
            + objects.sumOf {
                val contentStart: Int = it.findContentStart()
                return@sumOf getSizeAndTypeHeaderSize(it.bytes.size - contentStart, it.type) + it.bytes.size + 6
            }
            + Sha1Provider.SHA1_BYTES
        )

        val bytes: ByteArray = ByteArray(maximumTotalSize)

        writeHeader(objects, bytes)

        var head: Int = HEADER_SIZE
        for ((index, obj) in objects.withIndex()) {
            progressListener?.onProgress(index, objects.size)
            head += obj.writePackFileRepresentation(bytes, head)
        }

        sha1Provider.calculateSha1Hash(bytes, length = head).copyInto(bytes, head)
        head += Sha1Provider.SHA1_BYTES

        return PackFile(bytes, head)
    }

    private fun writeHeader(objects: Collection<GitObject>, output: ByteArray, writeOffset: Int = 0) {
        "PACK".encodeToByteArray().copyInto(output, writeOffset)
        GitConstants.GIT_VERSION.to4ByteArrayBigEndian(output, writeOffset + 4)
        objects.size.to4ByteArrayBigEndian(output, writeOffset + 8)
    }

    private suspend fun GitObject.writePackFileRepresentation(output: ByteArray, writeOffset: Int): Int {
        val contentStart: Int = findContentStart()
        val headerSize: Int = generateSizeAndTypeHeader(bytes.size - contentStart, type, output, writeOffset)
        val deflatedSize: Int =
            deflater.deflate(
                bytes,
                output,
                writeOffset = writeOffset + headerSize,
                inputStart = contentStart
            )
        return headerSize + deflatedSize
    }

    companion object {
        private const val HEADER_SIZE: Int = 4 + 4 + 4
    }
}
