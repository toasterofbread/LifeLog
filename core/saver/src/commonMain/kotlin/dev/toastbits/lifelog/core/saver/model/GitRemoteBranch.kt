package dev.toastbits.lifelog.core.saver.model

data class GitRemoteBranch(
    val remoteName: String,
    val remoteUrl: String,
    val branch: String
)
