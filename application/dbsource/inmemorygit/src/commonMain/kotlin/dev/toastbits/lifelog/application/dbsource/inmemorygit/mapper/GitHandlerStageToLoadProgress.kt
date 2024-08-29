package dev.toastbits.lifelog.application.dbsource.inmemorygit.mapper

import dev.toastbits.lifelog.application.dbsource.domain.accessor.DatabaseAccessor.LoadProgress
import dev.toastbits.lifelog.application.dbsource.domain.accessor.Network
import dev.toastbits.lifelog.core.git.handler.stage.GitHandlerStage
import lifelog.application.dbsource.inmemorygit.generated.resources.Res
import lifelog.application.dbsource.inmemorygit.generated.resources.accessor_progress_clone_pulling
import lifelog.application.dbsource.inmemorygit.generated.resources.accessor_progress_clone_retrieving_ref
import lifelog.application.dbsource.inmemorygit.generated.resources.accessor_progress_pack_file_parse_checksum
import lifelog.application.dbsource.inmemorygit.generated.resources.accessor_progress_pack_file_parse_parsing_objects
import lifelog.application.dbsource.inmemorygit.generated.resources.accessor_progress_pack_file_parse_preparing_pack_file
import lifelog.application.dbsource.inmemorygit.generated.resources.accessor_progress_pack_file_parse_reading_header
import lifelog.application.dbsource.inmemorygit.generated.resources.accessor_progress_rendering_commit_tree
import org.jetbrains.compose.resources.stringResource

internal fun GitHandlerStage.toLoadProgress(part: Long?, total: Long?): LoadProgress =
    when (this) {
        GitHandlerStage.Clone.RETRIEVE_REF -> LoadProgress.Network(part, total) { stringResource(Res.string.accessor_progress_clone_retrieving_ref) }
        GitHandlerStage.Clone.PULL -> LoadProgress.Network(part, total) { stringResource(Res.string.accessor_progress_clone_pulling) }
        GitHandlerStage.PackFileParse.PREPARE_PACK -> LoadProgress { stringResource(Res.string.accessor_progress_pack_file_parse_preparing_pack_file) }
        GitHandlerStage.PackFileParse.READ_HEADER -> LoadProgress { stringResource(Res.string.accessor_progress_pack_file_parse_reading_header) }
        GitHandlerStage.PackFileParse.PARSE_OBJECTS -> LoadProgress { stringResource(Res.string.accessor_progress_pack_file_parse_parsing_objects) }
        GitHandlerStage.PackFileParse.CHECKSUM -> LoadProgress { stringResource(Res.string.accessor_progress_pack_file_parse_checksum) }
        GitHandlerStage.RenderCommitTree -> LoadProgress { stringResource(Res.string.accessor_progress_rendering_commit_tree) }
    }