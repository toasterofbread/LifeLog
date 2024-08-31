package dev.toastbits.lifelog.core.git.memory.provider

import dev.toastbits.lifelog.core.git.memory.util.size
import org.kotlincrypto.hash.sha1.SHA1

class PlatformSha1Provider: Sha1Provider {
    private val sha1: SHA1 = SHA1()

    override fun calculateSha1Hash(
        bytes: ByteArray,
        offset: Int,
        length: Int
    ): ByteArray {
        sha1.update(bytes, offset, length)
        return sha1.digest()
    }

    override fun calculateSha1Hash(bytes: ByteArray, regions: List<IntRange>): ByteArray {
        for (region in regions) {
            sha1.update(bytes, region.first, region.size)
        }
        return sha1.digest()
    }
}
