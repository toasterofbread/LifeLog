package dev.toastbits.lifelog.helper

import dev.toastbits.lifelog.core.accessor.DatabaseFileStructureProvider
import dev.toastbits.lifelog.core.accessor.DatabaseFilesGenerator
import dev.toastbits.lifelog.core.accessor.DatabaseFilesParser
import dev.toastbits.lifelog.core.accessor.LogFileSplitStrategy
import dev.toastbits.lifelog.core.accessor.impl.DatabaseFileStructureProviderImpl
import dev.toastbits.lifelog.core.accessor.impl.DatabaseFilesGeneratorImpl
import dev.toastbits.lifelog.core.accessor.impl.DatabaseFilesParserImpl
import dev.toastbits.lifelog.core.accessor.reference.LogEntityReferenceGeneratorImpl
import dev.toastbits.lifelog.core.specification.converter.LogFileConverter
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.extension.Extendable
import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.core.specification.extension.SpecificationExtension
import dev.toastbits.lifelog.core.specification.impl.converter.LogFileConverterImpl
import dev.toastbits.lifelog.core.specification.impl.converter.LogFileConverterStringsImpl
import kotlinx.coroutines.CoroutineDispatcher

abstract class DatabaseHelperImpl internal constructor(
    ioDispatcher: CoroutineDispatcher,
    splitStrategy: LogFileSplitStrategy,
    strings: LogFileConverterStrings = LogFileConverterStringsImpl()
): Extendable {
    protected val fileStructureProvider: DatabaseFileStructureProvider =
        DatabaseFileStructureProviderImpl(strings, splitStrategy)
    protected val converter: LogFileConverter = LogFileConverterImpl(
        fileStructureProvider,
        { LogEntityReferenceGeneratorImpl(fileStructureProvider, it) })
    protected val parser: DatabaseFilesParser =
        DatabaseFilesParserImpl(converter, strings, fileStructureProvider, ioDispatcher)
    protected val generator: DatabaseFilesGenerator =
        DatabaseFilesGeneratorImpl(converter, fileStructureProvider, splitStrategy)

    private val extendables: List<Extendable> =
        listOf(fileStructureProvider, converter, parser, generator).filterIsInstance<Extendable>()

    override fun findRegisteredExtension(id: ExtensionId): SpecificationExtension? =
        converter.findRegisteredExtension(id)

    override fun registerExtension(extension: SpecificationExtension) {
        for (extendable in extendables) {
            extendable.registerExtension(extension)
        }
    }

    override fun unregisterExtension(extension: SpecificationExtension) {
        for (extendable in extendables) {
            extendable.unregisterExtension(extension)
        }
    }
}
