package dev.toastbits.lifelog.application.dbsource.inmemorygit.mapper

import dev.toastbits.lifelog.application.dbsource.domain.accessor.DatabaseAccessor.LoadProgress
import dev.toastbits.lifelog.application.dbsource.domain.accessor.create
import dev.toastbits.lifelog.core.git.memory.handler.stage.GitHandlerStage
import lifelog.application.dbsource.inmemorygit.generated.resources.Res
import lifelog.application.dbsource.inmemorygit.generated.resources.accessor_progress_clone_pulling
import lifelog.application.dbsource.inmemorygit.generated.resources.accessor_progress_clone_retrieving_ref
import lifelog.application.dbsource.inmemorygit.generated.resources.accessor_progress_pack_file_parse_checksum
import lifelog.application.dbsource.inmemorygit.generated.resources.accessor_progress_pack_file_parse_parsing_objects
import lifelog.application.dbsource.inmemorygit.generated.resources.accessor_progress_pack_file_parse_preparing_pack_file
import lifelog.application.dbsource.inmemorygit.generated.resources.accessor_progress_pack_file_parse_reading_header
import lifelog.application.dbsource.inmemorygit.generated.resources.accessor_progress_rendering_commit_tree
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal fun GitHandlerStage.toLoadProgress(part: Long?, total: Long?): LoadProgress {
    val type: LoadProgress.Type =
        when (this) {
            GitHandlerStage.Clone.RETRIEVE_REF -> LoadProgress.Type.NETWORK
            GitHandlerStage.Clone.PULL -> LoadProgress.Type.NETWORK
            GitHandlerStage.PackFileParse.PREPARE_PACK -> LoadProgress.Type.GENERIC
            GitHandlerStage.PackFileParse.READ_HEADER -> LoadProgress.Type.GENERIC
            GitHandlerStage.PackFileParse.PARSE_OBJECTS -> LoadProgress.Type.GENERIC
            GitHandlerStage.PackFileParse.CHECKSUM -> LoadProgress.Type.GENERIC
            GitHandlerStage.RenderCommitTree -> LoadProgress.Type.GENERIC
        }

    val messageResource: StringResource =
        when (this) {
            GitHandlerStage.Clone.RETRIEVE_REF -> Res.string.accessor_progress_clone_retrieving_ref
            GitHandlerStage.Clone.PULL -> Res.string.accessor_progress_clone_pulling
            GitHandlerStage.PackFileParse.PREPARE_PACK -> Res.string.accessor_progress_pack_file_parse_preparing_pack_file
            GitHandlerStage.PackFileParse.READ_HEADER -> Res.string.accessor_progress_pack_file_parse_reading_header
            GitHandlerStage.PackFileParse.PARSE_OBJECTS -> Res.string.accessor_progress_pack_file_parse_parsing_objects
            GitHandlerStage.PackFileParse.CHECKSUM -> Res.string.accessor_progress_pack_file_parse_checksum
            GitHandlerStage.RenderCommitTree -> Res.string.accessor_progress_rendering_commit_tree
        }

    return type.create(part, total, messageResource)
}
