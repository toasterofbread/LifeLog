@file:Suppress("ArrayInDataClass")
package dev.toastbits.lifelog.application.worker.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
actual data class SerialisableFileStructure(
    val root: Node.Directory
) {
    @Serializable
    sealed interface Node {
        @Serializable
        data class File(
            @Serializable(with = ByteArrayAsBase64Serializer::class)
            val content: ByteArray
        ): Node

        @Serializable
        data class Directory(
            val nodes: MutableMap<String, Node> = mutableMapOf()
        ): Node
    }
}

@OptIn(ExperimentalEncodingApi::class)
private object ByteArrayAsBase64Serializer: KSerializer<ByteArray> {
    private val base64: Base64 = Base64.Default

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(
            "ByteArrayAsBase64Serializer",
            PrimitiveKind.STRING
        )

    override fun serialize(encoder: Encoder, value: ByteArray) {
        val base64Encoded: String = base64.encode(value)
        encoder.encodeString(base64Encoded)
    }

    override fun deserialize(decoder: Decoder): ByteArray {
        val base64Decoded: String = decoder.decodeString()
        return base64.decode(base64Decoded)
    }
}
