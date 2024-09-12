package dev.toastbits.lifelog.core.plugin

import dev.toastbits.kotules.core.Kotule
import dev.toastbits.kotules.runtime.annotation.KotuleDeclaration
import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.core.specification.extension.SpecificationExtension
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType

@KotuleDeclaration
interface LifelogPlugin: Kotule, SpecificationExtension {
    override val id: ExtensionId
    override val extraEventTypes: List<LogEventType>
    override val extraInLogReferenceTypes: List<LogEntityReferenceType.InLog>
    override val extraInMetadataReferenceTypes: List<LogEntityReferenceType.InMetadata>
}
