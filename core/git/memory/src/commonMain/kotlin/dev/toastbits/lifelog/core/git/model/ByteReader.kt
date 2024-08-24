package dev.toastbits.lifelog.core.git.model

import dev.toastbits.lifelog.core.git.provider.ZlibInflater
import dev.toastbits.lifelog.core.git.util.ParserByteArray

internal class ByteReader(val bytes: ParserByteArray, var head: Int, val zlibInflater: ZlibInflater)
