package dev.toastbits.lifelog.core.saver.impl.git

import dev.toastbits.lifelog.core.git.GitWrapper
import dev.toastbits.lifelog.core.git.resolve
import dev.toastbits.lifelog.core.saver.LocalLogDatabaseSaver
import dev.toastbits.lifelog.core.saver.LogDatabaseFileStructureProvider
import dev.toastbits.lifelog.core.saver.RemoteLogDatabaseSaver
import dev.toastbits.lifelog.core.saver.LogFileSplitStrategy
import dev.toastbits.lifelog.core.saver.model.GitRemoteBranch
import dev.toastbits.lifelog.core.saver.splitDaysIntoGroups
import dev.toastbits.lifelog.core.specification.converter.GenerateAlertData
import dev.toastbits.lifelog.core.specification.converter.LogFileConverter
import dev.toastbits.lifelog.core.specification.database.LogDatabase
import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate
import okio.FileSystem
import okio.Path
import okio.SYSTEM

class GitLogDatabaseSaver(
    private val repository: GitWrapper,
    private val remote: GitRemoteBranch?,

    private val converter: LogFileConverter,
    private val fileStructureProvider: LogDatabaseFileStructureProvider,
    private val splitStrategy: LogFileSplitStrategy,
    private val fileSystem: FileSystem = FileSystem.SYSTEM
): LocalLogDatabaseSaver, RemoteLogDatabaseSaver {
    override suspend fun saveDatabaseLocally(database: LogDatabase, onAlert: (GenerateAlertData) -> Unit) {
        val dayGroups: List<List<LogDate>> = splitStrategy.splitDaysIntoGroups(database.days.keys)

        fileSystem.createDirectories(repository.directory)

        for (group in dayGroups) {
            val generateResult: LogFileConverter.GenerateResult =
                converter.generateLogFile(group.associateWith { database.days[it]!! })
            generateResult.alerts.forEach(onAlert)

            val logFile: Path = repository.resolve(fileStructureProvider.getLogFilePath(group.first().date))

            fileSystem.createDirectories(logFile.parent!!)
            fileSystem.write(logFile) {
                writeUtf8(generateResult.lines.joinToString("\n"))
            }
        }
    }

    override fun canSaveDatabaseRemotely(database: LogDatabase): Boolean =
        remote != null

    override suspend fun saveDatabaseRemotely(
        database: LogDatabase,
        message: String,
        onAlert: (GenerateAlertData) -> Unit
    ) {
        require(message.isNotBlank())
        checkNotNull(remote)

        repository.init()
        repository.remoteAdd(remote.remoteName, remote.remoteUrl)

        repository.fetch(remote.remoteName)

        if (repository.doesBranchExist("${remote.remoteName}/${remote.branch}")) {
            repository.checkout("${remote.remoteName}/${remote.branch}")
        }
        else {
            repository.checkoutOrphan(remote.branch)

            for (path in fileSystem.list(repository.directory)) {
                if (path.name == ".git") {
                    continue
                }
                fileSystem.deleteRecursively(path, mustExist = true)
            }
        }

        saveDatabaseLocally(database, onAlert)

        val changedFiles: List<Path> = repository.getUncommittedFiles()
        if (changedFiles.isEmpty()) {
            return
        }

        repository.add(".")
        repository.commit(message)
        repository.push(remote.remoteName, branch = remote.branch)
    }
}
