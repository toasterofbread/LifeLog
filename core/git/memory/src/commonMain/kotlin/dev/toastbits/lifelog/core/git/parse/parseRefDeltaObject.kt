package dev.toastbits.lifelog.core.git.parse

import dev.toastbits.lifelog.core.git.generate.generateGitObject
import dev.toastbits.lifelog.core.git.model.ByteReader
import dev.toastbits.lifelog.core.git.model.GitObject
import dev.toastbits.lifelog.core.git.model.GitObjectRegistry
import dev.toastbits.lifelog.core.git.model.MutableGitObjectRegistry
import dev.toastbits.lifelog.core.git.provider.PlatformZlibInflater
import dev.toastbits.lifelog.core.git.provider.Sha1Provider
import dev.toastbits.lifelog.core.git.provider.ZlibInflater
import dev.toastbits.lifelog.core.git.provider.createEmpty
import dev.toastbits.lifelog.core.git.util.ParserByteArray

internal fun ByteReader.parseRefDeltaObject(
    sha1Provider: Sha1Provider,
    objectRegistry: MutableGitObjectRegistry,
    zlibInflater: ZlibInflater
) {
    val objRef: String = bytes.toHexString(head, head + 20)
    head += 20

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

    val gitObject: GitObject = generateGitObject(obj.type, targetContent!!, sha1Provider)
    objectRegistry.writeObject(gitObject)
}
