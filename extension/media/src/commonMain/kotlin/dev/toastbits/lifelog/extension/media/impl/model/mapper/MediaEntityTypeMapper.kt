package dev.toastbits.lifelog.extension.media.impl.model.mapper

import dev.toastbits.lifelog.extension.media.impl.model.reference.BookMediaReference
import dev.toastbits.lifelog.extension.media.impl.model.reference.GameMediaReference
import dev.toastbits.lifelog.extension.media.impl.model.reference.MovieOrShowMediaReference
import dev.toastbits.lifelog.extension.media.impl.model.reference.SongMediaReference
import dev.toastbits.lifelog.extension.media.model.entity.event.BookMediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.entity.event.GameMediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.entity.event.MediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.entity.event.MovieOrShowMediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.entity.event.SongMediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.reference.MediaReference
import dev.toastbits.lifelog.extension.media.util.MediaEntityType

fun MediaEntityType.createReference(mediaId: String): MediaReference =
    when (this) {
        MediaEntityType.MOVIE_OR_SHOW -> MovieOrShowMediaReference(mediaId)
        MediaEntityType.BOOK -> BookMediaReference(mediaId)
        MediaEntityType.GAME -> GameMediaReference(mediaId)
        MediaEntityType.SONG -> SongMediaReference(mediaId)
    }

fun MediaEntityType.createConsumeEvent(mediaReference: MediaReference): MediaConsumeEvent =
    when (this) {
        MediaEntityType.MOVIE_OR_SHOW -> MovieOrShowMediaConsumeEvent(mediaReference)
        MediaEntityType.BOOK -> BookMediaConsumeEvent(mediaReference)
        MediaEntityType.GAME -> GameMediaConsumeEvent(mediaReference)
        MediaEntityType.SONG -> SongMediaConsumeEvent(mediaReference)
    }
