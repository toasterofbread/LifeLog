package dev.toastbits.lifelog.core.specification.model.reference

import kotlin.reflect.KClass

interface LogEntityReferenceType {
    val identifier: String
    val referenceClass: KClass<*>
}
