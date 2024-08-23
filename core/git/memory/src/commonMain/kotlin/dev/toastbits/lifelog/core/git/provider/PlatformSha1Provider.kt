package dev.toastbits.lifelog.core.git.provider

expect class PlatformSha1Provider: Sha1Provider {
    override fun calculateSha1Hash(bytes: ByteArray, offset: Int, length: Int): String
}
