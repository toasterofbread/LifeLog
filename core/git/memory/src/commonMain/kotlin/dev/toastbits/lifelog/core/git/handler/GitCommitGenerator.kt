package dev.toastbits.lifelog.core.git.handler

import dev.toastbits.lifelog.core.filestructure.FileStructure
import dev.toastbits.lifelog.core.git.generate.generateGitObject
import dev.toastbits.lifelog.core.git.model.GitObject
import dev.toastbits.lifelog.core.git.provider.Sha1Provider
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.format
import kotlinx.datetime.offsetAt

class GitCommitGenerator(
    private val treeGenerator: GitTreeGenerator,
    private val sha1Provider: Sha1Provider
) {
    data class UserInfo(
        val name: String,
        val email: String,
        val time: Instant,
        val timeZone: TimeZone
    ) {
        companion object {
            fun ofNow(name: String, email: String): UserInfo =
                UserInfo(
                    name,
                    email,
                    Clock.System.now(),
                    TimeZone.currentSystemDefault()
                )
        }
    }

    suspend fun generateCommitObject(
        baseCommit: GitObject,
        fileStructure: FileStructure,
        message: String,
        author: UserInfo,
        committer: UserInfo
    ): GitObject {
        val tree: GitObject = treeGenerator.getOrGenerateDirectoryTree(fileStructure.root)

        val content: ByteArray =
            buildString {
                appendLine("tree ${tree.hash}")
                appendLine("parent ${baseCommit.hash}")
                appendLine("author ${author.toCommitLine()}")
                appendLine("committer ${committer.toCommitLine()}")
                appendLine()
                append(message)
            }.encodeToByteArray()

        return generateGitObject(GitObject.Type.COMMIT, content, sha1Provider)
    }

    private fun UserInfo.toCommitLine(): String =
        "$name <$email> ${time.epochSeconds} ${timeZone.offsetAt(time).format(UtcOffset.Formats.FOUR_DIGITS)}"
}
