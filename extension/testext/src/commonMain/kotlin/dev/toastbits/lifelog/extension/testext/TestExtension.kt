package dev.toastbits.lifelog.extension.testext

import dev.toastbits.kotules.extension.annotation.KotuleDefinition
import dev.toastbits.lifelog.core.plugin.LifelogPlugin
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.converter.alert.LogGenerateAlert
import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceGenerator
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceParser
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType
import dev.toastbits.lifelog.core.specification.util.StringId

@KotuleDefinition
class TestExtension(): LifelogPlugin {
    override val id: ExtensionId = "Hello!"
    override val extraEventTypes: List<LogEventType>
        get() = listOf(
            object : LogEventType {
                override val name: StringId
                    get() = TODO("Not yet implemented 1")
                override val prefixes: List<String>
                    get() = TODO("Not yet implemented 2")
                override fun canGenerateEvent(event: LogEvent): Boolean {
                    TODO("Not yet implemented 3")
                }
                override fun parseEvent(
                    prefixIndex: Int,
                    body: String,
                    metadata: String?,
                    content: UserContent?,
                    referenceParser: LogEntityReferenceParser,
                    logStrings: LogFileConverterStrings,
                    onAlert: (LogParseAlert) -> Unit
                ): LogEvent {
                    TODO("Not yet implemented 4")
                }
                override fun generateEvent(
                    event: LogEvent,
                    referenceGenerator: LogEntityReferenceGenerator,
                    logStrings: LogFileConverterStrings,
                    onAlert: (LogGenerateAlert) -> Unit
                ): LogEventType.EventText {
                    TODO("Not yet implemented 5")
                }
            }
        )
    override val extraInLogReferenceTypes: List<LogEntityReferenceType.InLog>
        get() = TODO("Not yet implemented 6")
    override val extraInMetadataReferenceTypes: List<LogEntityReferenceType.InMetadata>
        get() = TODO("Not yet implemented 7")
}
