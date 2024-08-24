package dev.toastbits.lifelog.core.git.provider

import dev.toastbits.lifelog.core.git.util.size
import java.security.MessageDigest

actual class PlatformSha1Provider: Sha1Provider {
    private val sha1: MessageDigest by lazy { MessageDigest.getInstance("SHA-1") }
    
    actual override fun calculateSha1Hash(
        bytes: ByteArray,
        offset: Int,
        length: Int
    ): String {
        sha1.update(bytes, offset, length)
        return sha1.digest().toHexString()
    }

    actual override fun calculateSha1Hash(bytes: ByteArray, regions: List<IntRange>): String {
        for (region in regions) {
            sha1.update(bytes, region.first, region.size)
        }
        return sha1.digest().toHexString()
    }
}
