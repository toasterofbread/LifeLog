package dev.toastbits.lifelog.core.git.memory.handler.stage

sealed interface GitHandlerStage {
    enum class Clone: GitHandlerStage {
        RETRIEVE_REF,
        PULL
    }

    enum class PackFileParse: GitHandlerStage {
        PREPARE_PACK,
        READ_HEADER,
        PARSE_OBJECTS,
        CHECKSUM
    }

    data object RenderCommitTree: GitHandlerStage
}
