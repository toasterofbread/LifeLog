package dev.toastbits.lifelog.application.dbsource.source.type

interface DatabaseSourceType {
    fun isAvailableOnPlatform(): Boolean

    companion object {
        fun getAvailableOnPlatform(): List<DatabaseSourceType> =
            listOf(InMemoryGitDatabaseSourceType, LocalGitDatabaseSourceType)
    }
}
