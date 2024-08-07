package dev.toastbits.lifelog.extension.media.model.reference

import dev.toastbits.lifelog.extension.media.impl.model.mapper.createReference
import dev.toastbits.lifelog.extension.media.impl.model.reference.MovieOrShowMediaReference
import dev.toastbits.lifelog.extension.media.util.MediaEntityType
import dev.toastbits.lifelog.extension.media.util.MediaStringId
import dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType
import dev.toastbits.lifelog.core.specification.util.StringId

class MediaReferenceType: LogEntityReferenceType<MediaReference> {
    override val name: StringId = MediaStringId.MediaReferenceType.NAME

    enum class Prefixes {
        MEDIA;
    }

    override val prefixes: List<String> =
        Prefixes.entries.map { '/' + it.name }

    enum class MediaEntityTypePrefixes {
        MOVIE, FILM,
        SHOW, SERIES,
        BOOK, NOVEL,
        GAME,
        SONG, MUSIC;

        fun getMediaEntityType(): MediaEntityType =
            when (this) {
                MOVIE, FILM,
                SHOW, SERIES -> MediaEntityType.MOVIE_OR_SHOW
                BOOK, NOVEL -> MediaEntityType.BOOK
                GAME -> MediaEntityType.GAME
                SONG, MUSIC -> MediaEntityType.SONG
            }
    }

    override fun parseReference(reference: String, prefixIndex: Int, onAlert: (dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert) -> Unit): MediaReference? {
        val parts: List<String> = reference.split('/').drop(1)

        when (Prefixes.entries[prefixIndex]) {
            Prefixes.MEDIA -> {
                if (parts.size != 2) {
                    onAlert(dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert.InvalidReferenceSize)
                    return null
                }

                val (mediaTypeName: String, mediaId: String) = parts

                val mediaType: MediaEntityType? =
                    MediaEntityTypePrefixes.entries.firstOrNull { it.name.lowercase() == mediaTypeName.lowercase() }?.getMediaEntityType()

                if (mediaType == null) {
                    onAlert(dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert.InvalidReferenceFormat)
                    return null
                }

                return mediaType.createReference(mediaId)
            }
        }
    }
}
