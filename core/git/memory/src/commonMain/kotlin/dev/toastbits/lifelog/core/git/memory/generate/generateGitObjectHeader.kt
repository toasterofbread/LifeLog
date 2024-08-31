package dev.toastbits.lifelog.core.git.memory.generate

import dev.toastbits.lifelog.core.git.memory.model.GitObject
import dev.toastbits.lifelog.core.git.memory.provider.Sha1Provider
import dev.toastbits.lifelog.core.git.memory.util.GitConstants
import dev.toastbits.lifelog.core.git.memory.util.size

internal fun generateGitObject(
    type: GitObject.Type,
    content: ByteArray,
    sha1Provider: Sha1Provider,
    contentRange: IntRange = content.indices
): GitObject {
    val fullContent: ByteArray =
        generateGitObjectContent(
            type,
            content,
            contentRange
        )
    val hash: String = sha1Provider.calculateSha1Hash(fullContent).toHexString()
    return GitObject(fullContent, type, hash)
}

private fun generateGitObjectContent(type: GitObject.Type, content: ByteArray, contentRange: IntRange): ByteArray {
    val header: ByteArray =
        buildString {
            append(GitConstants.getObjectTypeIdentifier(type))
            append(' ')
            append(contentRange.size)
        }.encodeToByteArray()

    val output: ByteArray = ByteArray(header.size + contentRange.size + 1)
    header.copyInto(output)
    // 0b0
    content.copyInto(output, header.size + 1, startIndex = contentRange.first, endIndex = contentRange.last + 1)

    return output
}
