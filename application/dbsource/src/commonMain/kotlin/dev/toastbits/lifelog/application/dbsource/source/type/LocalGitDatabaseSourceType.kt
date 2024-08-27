package dev.toastbits.lifelog.application.dbsource.source.type

expect object LocalGitDatabaseSourceType: DatabaseSourceType {
    override fun isAvailableOnPlatform(): Boolean
}
