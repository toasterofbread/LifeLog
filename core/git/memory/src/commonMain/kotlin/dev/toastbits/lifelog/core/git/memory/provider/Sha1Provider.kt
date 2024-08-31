package dev.toastbits.lifelog.core.git.memory.provider

interface Sha1Provider {
    fun calculateSha1Hash(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size): ByteArray
    fun calculateSha1Hash(bytes: ByteArray, regions: List<IntRange>): ByteArray

    companion object {
        const val SHA1_BYTES: Int = 20
    }
}
