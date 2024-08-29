package dev.toastbits.lifelog.core.specification.extension

interface ExtensionRegistry {
    fun getAllExtensions(): List<SpecificationExtension>

    fun findRegisteredExtension(id: ExtensionId): SpecificationExtension?

    fun registerExtension(extension: SpecificationExtension)
    fun unregisterExtension(extension: SpecificationExtension)
}
