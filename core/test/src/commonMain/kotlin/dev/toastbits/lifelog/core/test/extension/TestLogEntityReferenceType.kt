package dev.toastbits.lifelog.core.test.extension

import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType
import kotlin.reflect.KClass

object TestLogEntityReferenceType: LogEntityReferenceType {
    override val identifier: String = "testlogentityreference"
    override val referenceClass: KClass<*> = TestLogEntityReference::class
}