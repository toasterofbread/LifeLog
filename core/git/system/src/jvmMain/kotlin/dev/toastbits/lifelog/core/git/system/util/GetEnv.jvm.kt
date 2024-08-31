package dev.toastbits.lifelog.core.git.system.util

actual fun getEnv(key: String): String? = System.getenv(key)
