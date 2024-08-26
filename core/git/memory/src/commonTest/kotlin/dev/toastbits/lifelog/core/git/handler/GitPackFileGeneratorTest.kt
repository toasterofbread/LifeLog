package dev.toastbits.lifelog.core.git.handler

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import dev.toastbits.lifelog.core.git.generate.generateGitObject
import dev.toastbits.lifelog.core.git.model.GitObject
import dev.toastbits.lifelog.core.git.model.MutableGitObjectRegistry
import dev.toastbits.lifelog.core.git.model.SimpleGitObjectRegistry
import dev.toastbits.lifelog.core.git.provider.PlatformSha1Provider
import dev.toastbits.lifelog.core.git.provider.PlatformZlibDeflater
import dev.toastbits.lifelog.core.git.provider.PlatformZlibInflater
import dev.toastbits.lifelog.core.git.provider.Sha1Provider
import dev.toastbits.lifelog.core.git.provider.ZlibDeflater
import dev.toastbits.lifelog.core.git.provider.ZlibInflater
import kotlin.test.BeforeTest
import kotlin.test.Test

class GitPackFileGeneratorTest {
    private lateinit var inflater: ZlibInflater
    private lateinit var deflater: ZlibDeflater
    private lateinit var sha1Provider: Sha1Provider

    @BeforeTest
    fun setUp() {
        inflater = PlatformZlibInflater(ByteArray(16777216))
        deflater = PlatformZlibDeflater()
        sha1Provider = PlatformSha1Provider()
    }

    @Test
    fun testPackFileGenerator() {
        val objects: MutableGitObjectRegistry = SimpleGitObjectRegistry()
        val testObjects: List<GitObject> =
            listOf(
                generateGitObject(GitObject.Type.BLOB, byteArrayOf(1, 2, 3, 4, 5), sha1Provider),
                generateGitObject(GitObject.Type.BLOB, byteArrayOf(6, 7, 8, 9, 10), sha1Provider)
            )

        for (obj in testObjects) {
            objects.writeObject(obj)
        }
        assertThat(objects.getAll().containsAll(testObjects)).isTrue()

        val inflater: ZlibInflater = PlatformZlibInflater(ByteArray(16777216))
        val deflater: ZlibDeflater = PlatformZlibDeflater()
        val sha1Provider: Sha1Provider = PlatformSha1Provider()

        val packFileGenerator = GitPackFileGenerator(sha1Provider, deflater)
        val packFile: GitPackFileGenerator.PackFile = packFileGenerator.generatePackFile(objects.getAll())

        val outputObjects: MutableGitObjectRegistry = SimpleGitObjectRegistry()
        val parser: GitPackFileParser = GitPackFileParser(sha1Provider, inflater, outputObjects)
        parser.parsePackFile(packFile.bytes, packFile.size)

        assertThat(outputObjects.getObjectsHashCode()).isEqualTo(objects.getObjectsHashCode())
        assertThat(outputObjects.getAll().containsAll(testObjects)).isTrue()
    }
}
