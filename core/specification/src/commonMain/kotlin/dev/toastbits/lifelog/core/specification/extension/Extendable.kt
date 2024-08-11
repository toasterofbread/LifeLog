package dev.toastbits.lifelog.core.specification.extension

interface Extendable {
    fun findRegisteredExtension(id: ExtensionId): SpecificationExtension?
    fun registerExtension(extension: SpecificationExtension)
    fun unregisterExtension(extension: SpecificationExtension)
}
