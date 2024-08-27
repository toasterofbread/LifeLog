package dev.toastbits.lifelog.application.dbsource.source.configuration

import dev.toastbits.lifelog.application.dbsource.source.type.DatabaseSourceType
import dev.toastbits.lifelog.application.dbsource.source.type.InMemoryGitDatabaseSourceType
import dev.toastbits.lifelog.application.dbsource.source.type.LocalGitDatabaseSourceType
import kotlinx.serialization.Serializable

@Serializable
sealed interface DatabaseSourceConfiguration {
    fun getType(): DatabaseSourceType

    @Serializable
    data class InMemoryGitDatabaseSourceConfiguration(val repositoryUrl: String): DatabaseSourceConfiguration {
        override fun getType(): DatabaseSourceType = InMemoryGitDatabaseSourceType
    }

    @Serializable
    data class LocalGitDatabaseSourceConfiguration(val repositoryPath: List<String>): DatabaseSourceConfiguration {
        override fun getType(): DatabaseSourceType = LocalGitDatabaseSourceType
    }
}
