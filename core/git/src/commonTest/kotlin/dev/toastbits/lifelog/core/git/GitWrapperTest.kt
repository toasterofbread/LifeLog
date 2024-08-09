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
import okio.FileSystem
import okio.Path
import okio.SYSTEM
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFails

class GitWrapperTest  {
    private lateinit var git: GitWrapper
    private lateinit var directory: Path

    private val fileSystem: FileSystem = FileSystem.SYSTEM

    @BeforeTest
    fun setUp() {
        directory = FileSystem.SYSTEM_TEMPORARY_DIRECTORY.resolve("lifelogtest")
        try {
            // Some operating systems (Windows) don't like this for some reason
            fileSystem.deleteRecursively(directory)
        }
        catch (_: Throwable) {
            var i: Int = 2
            while (fileSystem.exists(directory)) {
                directory = FileSystem.SYSTEM_TEMPORARY_DIRECTORY.resolve("lifelogtest-${i++}")
            }
        }

        git = GitWrapper.create(directory, UnconfinedTestDispatcher())
    }

    private inline fun checkGitInitialisation(initialise: () -> Unit) {
        assertThat(fileSystem.exists(directory)).isFalse()
        initialise()
        assertThat(fileSystem.exists(directory)).isTrue()
        assertThat(fileSystem.exists(directory.resolve(".git"))).isTrue()
    }

    @Test
    fun testInit() = runTest {
        checkGitInitialisation {
            git.init()
        }
    }

    @Test
    fun testClone() = runTest {
        for (remote in TEST_REMOTES) {
            setUp()

            checkGitInitialisation { git.clone(remote) }
            assertThat(fileSystem.exists(directory.resolve(TEST_REMOTE_EXISTING_FILE))).isTrue()
        }
    }

    @Test
    fun testInvalidGitClone() = runTest {
        assertFails {
            git.clone(NONEXISTENT_TEST_REMOTE)
        }
    }

    @Test
    fun testAddCommit() = runTest {
        checkGitInitialisation { git.init() }

        val file: String = "foo"
        val content: String = "bar"
        assertThat(file).isNotEqualTo(content)

        fileSystem.write(directory.resolve(file)) {
            writeUtf8(content)
        }
        fileSystem.read(directory.resolve(file)) {
            assertThat(readUtf8()).isEqualTo(content)
        }

        git.add(file)
        git.commit("baz")
    }

    @Test
    fun testInvalidGitAddCommit() = runTest {
        assertFails { git.add("foo") }
    }

    @Test
    fun testInvalidGitCommit() = runTest {
        assertFails { git.commit("foo") }
    }

    @Test
    fun testCheckout() = runTest {
        checkGitInitialisation { git.init() }

        val file: String = "foo"
        fileSystem.write(directory.resolve(file)) {
            writeUtf8("bar")
        }
        assertThat(fileSystem.exists(directory.resolve(file))).isTrue()

        git.add(file)
        git.commit(".")

        git.checkout("baz", createNew = true)
        assertThat(fileSystem.exists(directory.resolve(file))).isTrue()

        git.checkout("main", createNew = false)
        assertThat(fileSystem.exists(directory.resolve(file))).isTrue()
    }

    @Test
    fun testRemoteAddPull() = runTest {
        val remoteName: String = "origin"

        for (remote in TEST_REMOTES) {
            setUp()

            checkGitInitialisation { git.init() }
            git.remoteAdd(remoteName, remote)

            val file: Path = directory.resolve(TEST_REMOTE_EXISTING_FILE)
            assertThat(fileSystem.exists(file)).isFalse()
            git.pull(remoteName, TEST_REMOTE_EXISTING_BRANCH)
            assertThat(fileSystem.exists(file)).isTrue()
        }
    }

    @Test
    fun testInvalidGitAddPull() = runTest {
        val remoteName: String = "origin"

        checkGitInitialisation { git.init() }
        git.remoteAdd(remoteName, NONEXISTENT_TEST_REMOTE)

        assertFails {
            git.pull(remoteName, TEST_REMOTE_EXISTING_BRANCH)
        }
    }

    @Test
    fun testDoesBranchExist() = runTest {
        checkGitInitialisation { git.init() }

        fileSystem.write(git.directory.resolve("test")) { writeUtf8("") }
        git.add(".")
        git.commit(".")

        val branch: String = "test"
        git.checkout(branch, createNew = true)
        assertThat(git.doesBranchExist(branch)).isTrue()
    }

    @Test
    fun testDoesRemoteBranchExist() = runTest {
        checkGitInitialisation { git.init("notmain") }

        val remote: String = "origin"
        val branch: String = "$remote/$TEST_REMOTE_EXISTING_BRANCH"
        git.remoteAdd(remote, TEST_REMOTES.first())

        assertThat(git.doesBranchExist(branch)).isFalse()
        git.pull(remote, TEST_REMOTE_EXISTING_BRANCH)
        assertThat(git.doesBranchExist(branch)).isTrue()

        git.checkout(branch)
    }

    @Test
    fun testDoesInvalidRemoteBranchExist() = runTest {
        checkGitInitialisation { git.init("notmain") }

        val remote: String = "origin"
        git.remoteAdd(remote, TEST_REMOTES.first())

        assertThat(git.doesBranchExist(TEST_REMOTE_EXISTING_BRANCH)).isFalse()
        git.pull(remote, TEST_REMOTE_EXISTING_BRANCH)
        assertThat(git.doesBranchExist(TEST_REMOTE_NONEXISTENT_BRANCH)).isFalse()

        assertFails {
            git.checkout(TEST_REMOTE_NONEXISTENT_BRANCH)
        }
    }

    companion object {
        private val TEST_REMOTES: List<String> = listOf(
            "https://github.com/toasterofbread/kmp-template",
            "https://github.com/toasterofbread/kmp-template.git"
        )
        private const val TEST_REMOTE_EXISTING_FILE: String = "LICENSE"
        private const val TEST_REMOTE_EXISTING_BRANCH: String = "main"
        private const val TEST_REMOTE_NONEXISTENT_BRANCH: String = "jskxhdsjakndhxk"
        private const val NONEXISTENT_TEST_REMOTE: String = "https://google.com/"
    }
}
