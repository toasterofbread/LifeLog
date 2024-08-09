package dev.toastbits.lifelog.core.specification.extension

interface Extendable {
    fun registerExtension(extension: SpecificationExtension)
    fun unregisterExtension(extension: SpecificationExtension)
}
