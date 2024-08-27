package dev.toastbits.lifelog.application.dbsource.source.type

actual object LocalGitDatabaseSourceType : DatabaseSourceType {
    actual override fun isAvailableOnPlatform(): Boolean = false
}
