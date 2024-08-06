package dev.toastbits.lifelog.extension.media.impl.model.entity.event

import dev.toastbits.lifelog.extension.media.impl.model.mapper.createConsumeEvent
import dev.toastbits.lifelog.extension.media.impl.model.mapper.createReference
import dev.toastbits.lifelog.extension.media.model.entity.event.BookMediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.entity.event.GameMediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.entity.event.MediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.entity.event.MediaConsumeEventType
import dev.toastbits.lifelog.extension.media.model.entity.event.MovieOrShowMediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.entity.event.SongMediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.reference.MediaReference
import dev.toastbits.lifelog.extension.media.util.MediaEntityType
import dev.toastbits.lifelog.extension.media.util.MediaStringId
import dev.toastbits.lifelog.specification.converter.error.LogParseAlert
import dev.toastbits.lifelog.specification.model.UserContent

class MediaConsumeEventTypeImpl: MediaConsumeEventType {
    override val name: MediaStringId = MediaStringId.MediaExtension.NAME

    enum class Prefix {
        WATCHED,
        READ,
        PLAYED,
        LISTENED_TO;

        fun getEntityType(): MediaEntityType =
            when (this) {
                WATCHED -> MediaEntityType.MOVIE_OR_SHOW
                READ -> MediaEntityType.BOOK
                PLAYED -> MediaEntityType.GAME
                LISTENED_TO -> MediaEntityType.SONG
            }
    }

    override val prefixes: List<String> =
        Prefix.entries.map { it.name.replace('_', ' ') }

    override fun parseEvent(
        prefixIndex: Int,
        body: String,
        metadata: String?,
        content: UserContent,
        onAlert: (LogParseAlert) -> Unit
    ): MediaConsumeEvent {
        val entityType: MediaEntityType = Prefix.entries[prefixIndex].getEntityType()
        val mediaReference: MediaReference = entityType.createReference(body.trim())

        val event: MediaConsumeEvent = entityType.createConsumeEvent(mediaReference)
        event.content = content

        if (metadata != null) {
            applyEventMetadata(metadata, event, onAlert)
        }

        return event
    }

    private fun applyEventMetadata(
        text: String,
        event: MediaConsumeEvent,
        onAlert: (LogParseAlert) -> Unit
    ) {
        val parts: List<String> = text.split(',').filter { it.isNotBlank() }
        for (part in parts) {
            val lowerPart: String = part.lowercase()

            if (lowerPart.endsWith("watch")) {
                val iterationText: String = lowerPart.dropLast(5).trimEnd()
                applyEventIterationString(iterationText, event, onAlert)
            }
            else if (lowerPart.startsWith("ep ") || lowerPart.startsWith("eps ")) {
                val epsText: String = lowerPart.split(' ', limit = 2).getOrNull(1).orEmpty()
                applyEventEpisodesString(epsText, event, onAlert)
            }
        }
    }

    private fun applyEventIterationString(
        text: String,
        event: MediaConsumeEvent,
        onAlert: (LogParseAlert) -> Unit
    ) {
        var number: Int? =
            when (text) {
                "1st",
                "first" -> 1
                "2nd",
                "second" -> 1
                "3rd",
                "third" -> 1
                "fourth" -> 1
                "fifth" -> 1
                "sixth" -> 1
                "seventh" -> 1
                "eighth" -> 1
                "ninth" -> 1
                "tenth" -> 1
                else -> null
            }

        if (number == null && text.endsWith("rd")) {
            number = text.dropLast(2).trimEnd().toIntOrNull()
        }

        if (number == null) {
            onAlert(LogParseAlert.UnknownIterationSpecifier(text))
            return
        }

        event.iteration = number
    }


    private fun applyEventEpisodesString(
        text: String,
        event: MediaConsumeEvent,
        onAlert: (LogParseAlert) -> Unit
    ) {
        val splitChars: List<Char> = listOf('-', '~')

        val splitIndex: Int = text.indexOfFirst { splitChars.contains(it) }
        if (splitIndex == -1) {
            onAlert(LogParseAlert.InvalidEpisodesSpecifier(text))
            return
        }

        val lhsText: String = text.substring(0, splitIndex)
        val lastWhitespace: Int = lhsText.lastIndexOf(' ')
        val lhs: Int? = lhsText.substring(lastWhitespace + 1).toIntOrNull()
        if (lhs == null) {
            onAlert(LogParseAlert.InvalidEpisodesSpecifier(text))
            return
        }

        val rhsText: String = text.substring(splitIndex + 1, 0).split(' ', limit = 2).first()
        val rhs: Int? = rhsText.toIntOrNull()
        if (rhs == null) {
            onAlert(LogParseAlert.InvalidEpisodesSpecifier(text))
            return
        }

        TODO(text)
    }
}
