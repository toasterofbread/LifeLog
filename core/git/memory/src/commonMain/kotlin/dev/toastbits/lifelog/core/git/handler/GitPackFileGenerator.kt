package dev.toastbits.lifelog.core.git.handler

import dev.toastbits.lifelog.core.git.generate.generateSizeAndTypeHeader
import dev.toastbits.lifelog.core.git.generate.getSizeAndTypeHeaderSize
import dev.toastbits.lifelog.core.git.model.GitObject
import dev.toastbits.lifelog.core.git.provider.Sha1Provider
import dev.toastbits.lifelog.core.git.provider.ZlibDeflater
import dev.toastbits.lifelog.core.git.util.GitConstants
import dev.toastbits.lifelog.core.git.util.to4ByteArrayBigEndian

class GitPackFileGenerator(
    private val sha1Provider: Sha1Provider,
    private val deflater: ZlibDeflater
) {
    data class PackFile(val bytes: ByteArray, val size: Int)

    fun generatePackFile(objects: Collection<GitObject>): PackFile {
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
        for (obj in objects) {
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

    private fun GitObject.writePackFileRepresentation(output: ByteArray, writeOffset: Int): Int {
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
