package dev.toastbits.lifelog.core.git.memory.handler

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import dev.toastbits.lifelog.core.git.memory.generate.generateGitObject
import dev.toastbits.lifelog.core.git.memory.model.GitObject
import dev.toastbits.lifelog.core.git.memory.model.MutableGitObjectRegistry
import dev.toastbits.lifelog.core.git.memory.model.SimpleGitObjectRegistry
import dev.toastbits.lifelog.core.git.memory.provider.PlatformSha1Provider
import dev.toastbits.lifelog.core.git.memory.provider.PlatformZlibDeflater
import dev.toastbits.lifelog.core.git.memory.provider.PlatformZlibInflater
import dev.toastbits.lifelog.core.git.memory.provider.Sha1Provider
import dev.toastbits.lifelog.core.git.memory.provider.ZlibDeflater
import dev.toastbits.lifelog.core.git.memory.provider.ZlibInflater
import kotlinx.coroutines.test.runTest
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
    fun testPackFileGenerator() = runTest {
        val objects: MutableGitObjectRegistry = SimpleGitObjectRegistry()
        val testObjects: List<GitObject> =
            listOf(
                dev.toastbits.lifelog.core.git.memory.generate.generateGitObject(
                    GitObject.Type.BLOB,
                    "Hello".encodeToByteArray(),
                    sha1Provider
                ),
                dev.toastbits.lifelog.core.git.memory.generate.generateGitObject(
                    GitObject.Type.BLOB,
                    "World!".encodeToByteArray(),
                    sha1Provider
                )
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

        assertThat(outputObjects.getAll().containsAll(testObjects)).isTrue()
    }
}
