package dev.toastbits.lifelog.extension.media.impl.model.entity.event

import dev.toastbits.lifelog.core.specification.converter.LogFileConverterFormats
import dev.toastbits.lifelog.core.specification.converter.alert.LogGenerateAlert
import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceGenerator
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceParser
import dev.toastbits.lifelog.extension.media.converter.MediaExtensionConverterFormats
import dev.toastbits.lifelog.extension.media.impl.converter.MediaExtensionConverterFormatsImpl
import dev.toastbits.lifelog.extension.media.impl.model.mapper.createConsumeEvent
import dev.toastbits.lifelog.extension.media.impl.model.mapper.createReference
import dev.toastbits.lifelog.extension.media.model.entity.event.MediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.entity.event.MediaConsumeEventType
import dev.toastbits.lifelog.extension.media.model.reference.MediaReference
import dev.toastbits.lifelog.extension.media.util.MediaEntityType
import dev.toastbits.lifelog.extension.media.util.MediaStringId
import kotlin.reflect.KClass

class MediaConsumeEventTypeImpl(
    private val mediaFormats: MediaExtensionConverterFormats = MediaExtensionConverterFormatsImpl()
): MediaConsumeEventType {
    override val name: MediaStringId = MediaStringId.MediaExtension.NAME
    override val eventClass: KClass<*> = MediaConsumeEvent::class

    override val prefixes: List<String> =
        MediaEntityType.entries.flatMap { entityType ->
            mediaFormats.getMediaEntityTypeConsumeEventPrefixes(entityType).map { it.lowercase() }
        }

    override fun parseEvent(
        prefixIndex: Int,
        body: String,
        metadata: String?,
        content: UserContent?,
        referenceParser: LogEntityReferenceParser,
        formats: LogFileConverterFormats,
        onAlert: (LogParseAlert) -> Unit
    ): MediaConsumeEvent {
        val entityType: MediaEntityType = getPrefixIndexMediaEntityType(prefixIndex)
        val mediaReference: MediaReference = entityType.createReference(body.trim())

        val event: MediaConsumeEvent = entityType.createConsumeEvent(mediaReference)
        event.content = content

        if (metadata != null) {
            applyEventMetadata(metadata, event, mediaFormats, onAlert)
        }

        return event
    }

    override fun generateEvent(
        event: LogEvent,
        referenceGenerator: LogEntityReferenceGenerator,
        formats: LogFileConverterFormats,
        onAlert: (LogGenerateAlert) -> Unit
    ): LogEventType.EventText {
        check(event is MediaConsumeEvent)

        return LogEventType.EventText(
            prefix = mediaFormats.getMediaEntityTypeConsumeEventPrefixes(event.mediaEntityType).first(),
            body = getEventBodyText(event, referenceGenerator, onAlert),
            metadata = getEventMetadataText(event, referenceGenerator, formats, onAlert)
        )
    }

    private fun getEventBodyText(
        event: MediaConsumeEvent,
        referenceGenerator: LogEntityReferenceGenerator,
        onAlert: (LogGenerateAlert) -> Unit
    ): String =
        "[${event.mediaReference.mediaId}](<${referenceGenerator.generateReferencePath(event.mediaReference, onAlert = onAlert)}>)"

    private fun getEventMetadataText(
        event: MediaConsumeEvent,
        referenceGenerator: LogEntityReferenceGenerator,
        formats: LogFileConverterFormats,
        onAlert: (LogGenerateAlert) -> Unit
    ): String? = buildString {
        event.iteration?.also { iteration ->
            val iterationSuffix: String = mediaFormats.getMediaEntityTypeIterationSuffixes(event.mediaEntityType).first()
            val iterationText: String = formats.numberToIteration(iteration)
            append(iterationText)
            append(iterationSuffix)
        }

        // TODO eps
    }.takeIf { it.isNotBlank() }

    private fun applyEventMetadata(
        text: String,
        event: MediaConsumeEvent,
        formats: MediaExtensionConverterFormats,
        onAlert: (LogParseAlert) -> Unit
    ) {
        val iterationSuffixes: List<String> = formats.getMediaEntityTypeIterationSuffixes(event.mediaEntityType).map { it.lowercase() }

        val parts: List<String> = text.split(',').filter { it.isNotBlank() }
        for (part in parts) {
            val lowerPart: String = part.lowercase()

            for (suffix in iterationSuffixes) {
                if (!lowerPart.endsWith(suffix)) {
                    continue
                }

                val iterationText: String = lowerPart.dropLast(suffix.length).trimEnd()
                applyEventIterationString(iterationText, event, onAlert)
                break
            }

            if (lowerPart.startsWith("ep ") || lowerPart.startsWith("eps ")) {
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
                "second" -> 2
                "3rd",
                "third" -> 3
                "fourth" -> 4
                "fifth" -> 5
                "sixth" -> 6
                "seventh" -> 7
                "eighth" -> 8
                "ninth" -> 9
                "tenth" -> 10
                else -> null
            }

        if (number == null && text.endsWith("th")) {
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

    private fun getPrefixIndexMediaEntityType(index: Int): MediaEntityType {
        var currentIndex: Int = index
        for (entityType in MediaEntityType.entries) {
            if (currentIndex <= 0) {
                return entityType
            }
            currentIndex -= mediaFormats.getMediaEntityTypeConsumeEventPrefixes(entityType).size
        }
        if (currentIndex <= 0) {
            return MediaEntityType.entries.last()
        }

        throw IllegalStateException(index.toString())
    }
}
