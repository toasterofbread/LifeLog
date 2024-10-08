package dev.toastbits.lifelog.core.specification.impl.extension

import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.core.specification.extension.ExtensionRegistry
import dev.toastbits.lifelog.core.specification.extension.SpecificationExtension
import dev.toastbits.lifelog.core.specification.extension.validate

class ExtensionRegistryImpl(extensions: List<SpecificationExtension> = emptyList()): ExtensionRegistry {
    private val extensions: MutableList<SpecificationExtension> = extensions.toMutableList()

    override fun findRegisteredExtension(id: ExtensionId): SpecificationExtension? =
        extensions.firstOrNull { it.id == id }

    override fun getAllExtensions(): List<SpecificationExtension> = extensions

    override fun registerExtension(extension: SpecificationExtension) {
        extension.validate()
        extensions.add(extension)
    }

    override fun unregisterExtension(extension: SpecificationExtension) {
        extensions.remove(extension)
    }

    override fun toString(): String =
        "ExtensionRegistryImpl(extensions=${extensions.toList()})"
}
