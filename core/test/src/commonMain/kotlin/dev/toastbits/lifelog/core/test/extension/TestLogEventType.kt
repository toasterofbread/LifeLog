package dev.toastbits.lifelog.core.test.extension

import dev.toastbits.lifelog.core.specification.converter.LogFileConverterFormats
import dev.toastbits.lifelog.core.specification.converter.alert.LogGenerateAlert
import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceGenerator
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceParser
import dev.toastbits.lifelog.core.specification.util.StringId
import kotlin.reflect.KClass

object TestLogEventType: LogEventType {
    const val PREFIX: String = "testprefix "

    override val name: StringId get() = TODO()
    override val prefixes: List<String> = listOf(PREFIX)
    override val eventClass: KClass<*> = TestLogEvent::class

    override fun parseEvent(
        prefixIndex: Int,
        body: String,
        metadata: String?,
        content: UserContent?,
        referenceParser: LogEntityReferenceParser,
        formats: LogFileConverterFormats,
        onAlert: (LogParseAlert) -> Unit
    ): LogEvent {
        TODO("Not yet implemented")
    }

    override fun generateEvent(
        event: LogEvent,
        referenceGenerator: LogEntityReferenceGenerator,
        formats: LogFileConverterFormats,
        onAlert: (LogGenerateAlert) -> Unit
    ): LogEventType.EventText {
        check(event is TestLogEvent)
        return LogEventType.EventText(
            prefix = "PREFIX",
            body = "[${event.reference.entityPath.segments.last()}](<${referenceGenerator.generateReferencePath(event.reference, onAlert = onAlert)}>)",
            metadata = "METADATA"
        )
    }
}
