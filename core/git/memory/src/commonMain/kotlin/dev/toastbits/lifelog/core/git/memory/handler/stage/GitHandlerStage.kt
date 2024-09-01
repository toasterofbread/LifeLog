package dev.toastbits.lifelog.core.git.memory.handler.stage

import kotlinx.serialization.Serializable

@Serializable
sealed interface GitHandlerStage {
    @Serializable
    enum class Clone: GitHandlerStage {
        RETRIEVE_REF,
        PULL
    }

    @Serializable
    enum class PackFileParse: GitHandlerStage {
        PREPARE_PACK,
        READ_HEADER,
        PARSE_OBJECTS,
        CHECKSUM
    }

    @Serializable
    data object RenderCommitTree: GitHandlerStage

    @Serializable
    data object SerialisingFileStructure: GitHandlerStage
}
