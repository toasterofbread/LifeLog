package dev.toastbits.lifelog.core.test.extension

import dev.toastbits.lifelog.core.specification.converter.ParseAlertData
import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.database.LogEntityMetadata
import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityPath
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType

object TestLogEntityReferenceType: LogEntityReferenceType {
    override val id: ExtensionId = "testlogentityreference"
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
            extensionId = TestExtension.id,
            referenceTypeId = id
        )
    }

    override fun parseReferenceMetadata(
        path: List<String>,
        lines: Sequence<String>,
        onAlert: (ParseAlertData) -> Unit
    ): LogEntityMetadata {
        return LogEntityMetadata("TODO")
    }
}