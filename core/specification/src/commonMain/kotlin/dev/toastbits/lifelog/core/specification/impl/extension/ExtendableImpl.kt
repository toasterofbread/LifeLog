package dev.toastbits.lifelog.core.specification.impl.extension

import dev.toastbits.lifelog.core.specification.extension.Extendable
import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.core.specification.extension.SpecificationExtension
import dev.toastbits.lifelog.core.specification.extension.validate

abstract class ExtendableImpl: Extendable {
    protected val extensions: MutableList<SpecificationExtension> = mutableListOf()

    override fun findRegisteredExtension(id: ExtensionId): SpecificationExtension? =
        extensions.firstOrNull { it.id == id }

    override fun registerExtension(extension: SpecificationExtension) {
        extension.validate()
        extensions.add(extension)
    }

    override fun unregisterExtension(extension: SpecificationExtension) {
        extensions.remove(extension)
    }
}
