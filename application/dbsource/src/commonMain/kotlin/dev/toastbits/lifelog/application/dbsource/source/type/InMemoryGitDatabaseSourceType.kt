package dev.toastbits.lifelog.application.dbsource.source.type

object InMemoryGitDatabaseSourceType: DatabaseSourceType {
    override fun isAvailableOnPlatform(): Boolean = true
}
