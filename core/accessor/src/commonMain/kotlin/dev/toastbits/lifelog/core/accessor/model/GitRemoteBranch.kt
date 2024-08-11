package dev.toastbits.lifelog.core.accessor.model

data class GitRemoteBranch(
    val remoteName: String,
    val remoteUrl: String,
    val branch: String
)
