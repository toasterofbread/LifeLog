package dev.toastbits.lifelog.core.git.provider

import java.security.MessageDigest

actual class PlatformSha1Provider: Sha1Provider {
    private val sha1: MessageDigest by lazy { MessageDigest.getInstance("SHA-1") }
    
    @OptIn(ExperimentalStdlibApi::class)
    actual override fun calculateSha1Hash(
        bytes: ByteArray,
        offset: Int,
        length: Int
    ): String {
        sha1.update(bytes, offset, length)
        return sha1.digest().toHexString()
    }
}
