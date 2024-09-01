package dev.toastbits.lifelog.core.git.core.model

import kotlinx.serialization.Serializable

@Serializable
data class GitCredentials(val username: String, val password: String)
