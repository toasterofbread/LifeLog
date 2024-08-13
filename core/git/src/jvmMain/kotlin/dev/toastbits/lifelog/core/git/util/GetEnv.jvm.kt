package dev.toastbits.lifelog.core.git.util

actual fun getEnv(key: String): String? = System.getenv(key)
