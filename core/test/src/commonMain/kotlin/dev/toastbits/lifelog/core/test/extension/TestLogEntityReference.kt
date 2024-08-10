package dev.toastbits.lifelog.core.test.extension

import dev.toastbits.lifelog.core.specification.model.reference.LogEntityPath
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import kotlin.reflect.KClass

class TestLogEntityReference(
    override val entityPath: LogEntityPath
) : LogEntityReference {
    override val entityTypeClass: KClass<*> = TestLogEntityReferenceType::class
}
