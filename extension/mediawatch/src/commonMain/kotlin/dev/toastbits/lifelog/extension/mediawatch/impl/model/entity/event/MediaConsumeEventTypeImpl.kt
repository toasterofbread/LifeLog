package dev.toastbits.lifelog.extension.mediawatch.impl.model.entity.event

import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.converter.alert.LogGenerateAlert
import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceGenerator
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceParser
import dev.toastbits.lifelog.extension.mediawatch.MediaWatchExtensionStrings
import dev.toastbits.lifelog.extension.mediawatch.impl.model.mapper.createConsumeEvent
import dev.toastbits.lifelog.extension.mediawatch.impl.model.mapper.createReference
import dev.toastbits.lifelog.extension.mediawatch.model.entity.event.MediaConsumeEvent
import dev.toastbits.lifelog.extension.mediawatch.model.entity.event.MediaConsumeEventType
import dev.toastbits.lifelog.extension.mediawatch.model.reference.MediaReference
import dev.toastbits.lifelog.extension.mediawatch.util.MediaEntityType
import dev.toastbits.lifelog.extension.mediawatch.util.MediaStringId
import kotlin.reflect.KClass

class MediaConsumeEventTypeImpl(
    private val strings: MediaWatchExtensionStrings
): MediaConsumeEventType {
    override val name: MediaStringId = MediaStringId.MediaExtension.NAME
    override val eventClass: KClass<*> = MediaConsumeEvent::class

    override val prefixes: List<String> =
        MediaEntityType.entries.flatMap { entityType ->
            strings.getMediaEntityTypeConsumeEventPrefixes(entityType).map { it.lowercase() }
        }

    override fun parseEvent(
        prefixIndex: Int,
        body: String,
        metadata: String?,
        content: UserContent?,
        referenceParser: LogEntityReferenceParser,
        logStrings: LogFileConverterStrings,
        onAlert: (LogParseAlert) -> Unit
    ): MediaConsumeEvent {
        val entityType: MediaEntityType = getPrefixIndexMediaEntityType(prefixIndex)
        val mediaReference: MediaReference = entityType.createReference(body.trim(), this.strings.extensionId, this.strings.mediaReferenceTypeId)

        val event: MediaConsumeEvent = entityType.createConsumeEvent(mediaReference)
        event.content = content

        if (metadata != null) {
            applyEventMetadata(metadata, event,
                this.strings, logStrings, onAlert)
        }

        return event
    }

    override fun generateEvent(
        event: LogEvent,
        referenceGenerator: LogEntityReferenceGenerator,
        logStrings: LogFileConverterStrings,
        onAlert: (LogGenerateAlert) -> Unit
    ): LogEventType.EventText {
        check(event is MediaConsumeEvent)

        return LogEventType.EventText(
            prefix = this.strings.getMediaEntityTypeConsumeEventPrefixes(event.mediaEntityType).first(),
            body = getEventBodyText(event, referenceGenerator, onAlert),
            metadata = getEventMetadataText(event, referenceGenerator, logStrings, onAlert)
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
        logStrings: LogFileConverterStrings,
        onAlert: (LogGenerateAlert) -> Unit
    ): String? = listOfNotNull(
        event.iteration?.let { iteration ->
            val iterationSuffix: String = strings.getMediaEntityTypeIterationSuffixes(event.mediaEntityType).first()
            val iterationText: String = logStrings.numberToIteration(iteration)
            iterationText + iterationSuffix
        },
        event.generateMediaRangeMetadata(strings, logStrings)
    ).filter { it.isNotBlank() }.ifEmpty { null }?.joinToString(", ")

    private fun getPrefixIndexMediaEntityType(index: Int): MediaEntityType {
        var currentIndex: Int = index
        for (entityType in MediaEntityType.entries) {
            if (currentIndex <= 0) {
                return entityType
            }
            currentIndex -= strings.getMediaEntityTypeConsumeEventPrefixes(entityType).size
        }
        if (currentIndex <= 0) {
            return MediaEntityType.entries.last()
        }

        throw IllegalStateException(index.toString())
    }
}
