@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.toastbits.lifelog.core.git

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEqualTo
import assertk.assertions.isTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import okio.Path
import okio.SYSTEM
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFails

class GitWrapperTest  {
    private lateinit var git: GitWrapper
    private lateinit var directory: Path

    private val system: FileSystem = FileSystem.SYSTEM

    @BeforeTest
    fun setUp() {
        var i: Int = 1
        do {
            directory = FileSystem.SYSTEM_TEMPORARY_DIRECTORY.resolve("lifelogtest-${i++}")
        }
        while (FileSystem.SYSTEM.exists(directory))

        git = GitWrapper.create(directory, UnconfinedTestDispatcher())
    }

    private inline fun checkGitInitialisation(initialise: () -> Unit) {
        assertThat(system.exists(directory)).isFalse()
        initialise()
        assertThat(system.exists(directory)).isTrue()
        assertThat(system.exists(directory.resolve(".git"))).isTrue()
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
            assertThat(system.exists(directory.resolve(TEST_REMOTE_EXISTING_FILE))).isTrue()
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

        system.write(directory.resolve(file)) {
            writeUtf8(content)
        }
        system.read(directory.resolve(file)) {
            assertThat(readUtf8()).isEqualTo(content)
        }

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

            val file: Path = directory.resolve(TEST_REMOTE_EXISTING_FILE)
            assertThat(system.exists(file)).isFalse()
            git.pull(remoteName, TEST_REMOTE_DEFAULT_BRANCH)
            assertThat(system.exists(file)).isTrue()
        }
    }

    @Test
    fun testInvalidGitAddPull() = runTest {
        val remoteName: String = "origin"

        checkGitInitialisation { git.init() }
        git.remoteAdd(remoteName, NONEXISTENT_TEST_REMOTE)

        assertFails {
            git.pull(remoteName, TEST_REMOTE_DEFAULT_BRANCH)
        }
    }

    companion object {
        private val TEST_REMOTES: List<String> = listOf(
            "https://github.com/toasterofbread/kmp-template",
            "https://github.com/toasterofbread/kmp-template.git"
        )
        private const val TEST_REMOTE_EXISTING_FILE: String = "LICENSE"
        private const val TEST_REMOTE_DEFAULT_BRANCH: String = "main"
        private const val NONEXISTENT_TEST_REMOTE: String = "https://google.com/"
    }
}
