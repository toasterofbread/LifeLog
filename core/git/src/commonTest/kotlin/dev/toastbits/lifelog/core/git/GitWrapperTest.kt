@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.toastbits.lifelog.core.git

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEqualTo
import assertk.assertions.isTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFails

class GitWrapperTest  {
    private lateinit var git: GitWrapper
    private lateinit var directory: File

    @BeforeTest
    fun setUp() {
        val os: String = System.getProperty("os.name")!!.lowercase()
        directory = when  {
            os == "linux" -> File("/tmp/lifelogtest")
            else -> throw NotImplementedError(os)
        }
        directory.deleteRecursively()

        git = GitWrapper(directory, UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        directory.deleteRecursively()
    }

    private inline fun checkGitInitialisation(initialise: () -> Unit) {
        assertThat(directory.exists()).isFalse()
        initialise()
        assertThat(directory.isDirectory).isTrue()
        assertThat(directory.resolve(".git").isDirectory).isTrue()
    }

    @Test
    fun testGitInit() = runTest {
        checkGitInitialisation {
            git.init()
        }
    }

    @Test
    fun testGitClone() = runTest {
        for (remote in TEST_REMOTES) {
            setUp()

            checkGitInitialisation { git.clone(remote) }
            assertThat(directory.resolve(TEST_REMOTE_EXISTING_FILE).isFile).isTrue()
        }
    }

    @Test
    fun testInvalidGitClone() = runTest {
        assertFails {
            git.clone(NONEXISTENT_TEST_REMOTE)
        }
    }

    @Test
    fun testGitAddCommit() = runTest {
        checkGitInitialisation { git.init() }

        val file: String = "foo"
        val content: String = "bar"
        assertThat(file).isNotEqualTo(content)

        directory.resolve(file).writeText(content)
        assertThat(directory.resolve(file).readText()).isEqualTo(content)

        git.add(file)
        git.commit("baz")
    }

    @Test
    fun testInvalidGitAddCommit() = runTest {
        assertFails { git.add("foo") }
        assertFails { git.commit("foo") }
    }

    @Test
    fun testGitRemoteAddPull() = runTest {
        val remoteName: String = "origin"

        for (remote in TEST_REMOTES) {
            setUp()

            checkGitInitialisation { git.init() }
            git.remoteAdd(remoteName, remote)

            assertThat(directory.resolve(TEST_REMOTE_EXISTING_FILE).exists()).isFalse()
            git.pull(remoteName)
            assertThat(directory.resolve(TEST_REMOTE_EXISTING_FILE).isFile).isTrue()
        }
    }

    @Test
    fun testInvalidGitAddPull() = runTest {
        val remoteName: String = "origin"

        checkGitInitialisation { git.init() }
        git.remoteAdd(remoteName, NONEXISTENT_TEST_REMOTE)

        assertFails {
            git.pull(remoteName)
        }
    }

    companion object {
        private val TEST_REMOTES: List<String> = listOf(
            "https://github.com/toasterofbread/kmp-template",
            "https://github.com/toasterofbread/kmp-template.git"
        )
        private const val TEST_REMOTE_EXISTING_FILE: String = "LICENSE"
        private const val NONEXISTENT_TEST_REMOTE: String = "https://google.com/"
    }
}
