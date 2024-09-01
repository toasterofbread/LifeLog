package dev.toastbits.lifelog.application.worker.model

import dev.toastbits.lifelog.core.filestructure.FileStructure
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
actual class SerialisableFileStructure(@Serializable(with = DummySerializer::class) val fileStructure: FileStructure)

private object DummySerializer: KSerializer<FileStructure> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(
            "DummySerializer",
            PrimitiveKind.STRING
        )

    override fun serialize(encoder: Encoder, value: FileStructure) =
        throw IllegalStateException()

    override fun deserialize(decoder: Decoder): FileStructure =
        throw IllegalStateException()
}

