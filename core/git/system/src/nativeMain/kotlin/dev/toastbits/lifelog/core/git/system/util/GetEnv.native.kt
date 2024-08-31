package dev.toastbits.lifelog.core.git.system.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
actual fun getEnv(key: String): String? = getenv(key)?.toKString()
