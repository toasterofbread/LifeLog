package dev.toastbits.lifelog.core.test.extension

import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityPath
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType
import kotlin.reflect.KClass

object TestLogEntityReferenceType: LogEntityReferenceType {
    override val identifier: String = "testlogentityreference"
    override val extensionId: ExtensionId get() = TestExtension.id

    override fun parseReference(
        path: List<String>,
        onAlert: (LogParseAlert) -> Unit
    ): LogEntityReference? {
        if (path.isEmpty()) {
            onAlert(LogParseAlert.UnknownReferenceType(path, 0))
            return null
        }

        return LogEntityReference.InMetadataData(
            path = LogEntityPath(path),
            extensionId = TestExtension.id
        )
    }
}