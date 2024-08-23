package dev.toastbits.lifelog.core.git.model

import dev.toastbits.lifelog.core.git.provider.ZlibInflater

internal class ByteReader(val bytes: ByteArray, var head: Int, val zlibInflater: ZlibInflater)
