package dev.toastbits.lifelog.core.git.memory.model

import dev.toastbits.lifelog.core.git.memory.provider.ZlibInflater
import dev.toastbits.lifelog.core.git.memory.util.ParserByteArray

internal class ByteReader(val bytes: ParserByteArray, var head: Int, val zlibInflater: ZlibInflater)
