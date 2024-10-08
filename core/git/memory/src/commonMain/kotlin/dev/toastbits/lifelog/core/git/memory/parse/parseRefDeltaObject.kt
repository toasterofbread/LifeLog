package dev.toastbits.lifelog.core.git.memory.parse

import dev.toastbits.lifelog.core.git.memory.model.ByteReader
import dev.toastbits.lifelog.core.git.memory.model.GitObject
import dev.toastbits.lifelog.core.git.memory.model.MutableGitObjectRegistry
import dev.toastbits.lifelog.core.git.memory.model.readObject
import dev.toastbits.lifelog.core.git.memory.provider.PlatformZlibInflater
import dev.toastbits.lifelog.core.git.memory.provider.Sha1Provider
import dev.toastbits.lifelog.core.git.memory.provider.ZlibInflater
import dev.toastbits.lifelog.core.git.memory.util.ParserByteArray

internal suspend fun ByteReader.parseRefDeltaObject(
    sha1Provider: Sha1Provider,
    objectRegistry: MutableGitObjectRegistry,
    zlibInflater: ZlibInflater
) {
    val objRef: String = bytes.toHexString(head, head + Sha1Provider.SHA1_BYTES)
    head += Sha1Provider.SHA1_BYTES

    val obj: GitObject = objectRegistry.readObject(objRef)
    val contentSize: Int = parseContent(null)
    val content: ParserByteArray = ParserByteArray(zlibInflater.outputBytes)

    val contentReader: ByteReader = ByteReader(content, 0, PlatformZlibInflater.createEmpty())

    contentReader.parseSizeHeader()
    contentReader.parseSizeHeader()

    var targetContent: ByteArray? = null

    // TODO | Cleanup
    while (contentReader.head < contentSize) {
        val isCopy: Boolean = (content[contentReader.head].toUByte().toLong() and 0b10000000) != 0L
        if (isCopy) {
            var dataPtr: Int = contentReader.head + 1
            var offset: Long = 0
            var size: Long = 0
            for (i in 0 until 4) {
                if ((content[contentReader.head].toUByte().toLong() and (1L shl i)) != 0L) {
                    offset = offset or (content[dataPtr++].toUByte().toLong() shl (i * 8))
                }
            }
            for (i in 0 until 3) {
                if ((content[contentReader.head].toUByte().toLong() and (1L shl (i + 4))) != 0L) {
                    size = size or (content[dataPtr++].toUByte().toLong() shl (i * 8))
                }
            }
            contentReader.head = dataPtr

            val contentStart: Int = obj.findContentStart()
            val nextContent: ByteArray = obj.bytes.sliceArray(contentStart + offset.toInt() until contentStart + (offset + size).toInt())
            targetContent = targetContent?.let { it + nextContent } ?: nextContent
        }
        else {
            val size = content[contentReader.head].toInt()
            val append = content.sliceArray(contentReader.head + 1 until contentReader.head + size + 1)
            contentReader.head += size + 1
            targetContent = targetContent?.let { it + append } ?: append
        }
    }

    val gitObject: GitObject = dev.toastbits.lifelog.core.git.memory.generate.generateGitObject(
        obj.type,
        targetContent!!,
        sha1Provider
    )
    objectRegistry.writeObject(gitObject)
}
