package dev.toastbits.lifelog.core.git.generate

import dev.toastbits.lifelog.core.git.model.GitObject
import dev.toastbits.lifelog.core.git.provider.Sha1Provider
import dev.toastbits.lifelog.core.git.util.GitConstants

internal fun generateGitObject(
    type: GitObject.Type,
    content: ByteArray,
    sha1Provider: Sha1Provider,
    contentSize: Int = content.size
): GitObject {
    val fullContent: ByteArray = generateGitObjectContent(type, content, contentSize)
    val hash: String = sha1Provider.calculateSha1Hash(fullContent)
    return GitObject(fullContent, type, hash)
}

private fun generateGitObjectContent(type: GitObject.Type, content: ByteArray, contentSize: Int = content.size): ByteArray {
    val header: ByteArray =
        buildString {
            append(GitConstants.getObjectTypeIdentifier(type))
            append(' ')
            append(contentSize)
        }.encodeToByteArray()

    val output: ByteArray = ByteArray(header.size + contentSize + 1)
    header.copyInto(output)
    // 0b0
    content.copyInto(output, header.size + 1, endIndex = contentSize)

    return output
}
