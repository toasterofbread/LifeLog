package dev.toastbits.lifelog.core.git.provider

interface Sha1Provider {
    fun calculateSha1Hash(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size): String
}
