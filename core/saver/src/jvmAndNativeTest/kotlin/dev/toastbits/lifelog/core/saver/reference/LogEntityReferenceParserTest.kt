package dev.toastbits.lifelog.core.saver.reference

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import dev.toastbits.lifelog.core.saver.LogDatabaseFileStructureProvider
import dev.toastbits.lifelog.core.saver.LogFileSplitStrategy
import dev.toastbits.lifelog.core.saver.impl.LogDatabaseFileStructureProviderImpl
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.impl.converter.LogFileConverterStringsImpl
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityPath
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceParser
import dev.toastbits.lifelog.core.test.extension.TestExtension
import dev.toastbits.lifelog.core.test.extension.TestLogEntityReferenceType
import kotlin.test.BeforeTest
import kotlin.test.Test

class LogEntityReferenceParserTest {
    private val strings: LogFileConverterStrings = LogFileConverterStringsImpl()
    private lateinit var fileStructureProvider: LogDatabaseFileStructureProvider
    private lateinit var referenceParser: LogEntityReferenceParser

    @BeforeTest
    fun setUp() {
        fileStructureProvider = LogDatabaseFileStructureProviderImpl(
            strings,
            LogFileSplitStrategy.Month
        )
        fileStructureProvider.registerExtension(TestExtension)
        referenceParser = fileStructureProvider
    }

    @Test
    fun testValidPath() {
        val testEntityPath: LogEntityPath = LogEntityPath.of("test path 1", "test path 2", "test path 3")
        val path: String = "${strings.metadataDirectoryName}/${strings.metadataExtensionDirectoryName}/${TestExtension.name}/${TestLogEntityReferenceType.identifier}/" + testEntityPath.segments.joinToString("/")

        val result: LogEntityReference? = referenceParser.parseReference(path) { assertThat(it).isNull() }
        assertThat(result).isEqualTo(
            LogEntityReference.InMetadataData(testEntityPath, TestExtension.id)
        )
    }

    @Test
    fun testInvalidMetadataPath() {
        testInvalidPath(
            "${strings.metadataDirectoryName}uistbdstacbybu/${strings.metadataExtensionDirectoryName}/${TestExtension.name}/${TestLogEntityReferenceType.identifier}",
            0
        )
    }

    @Test
    fun testInvalidMetadataSubPath() {
        testInvalidPath(
            "${strings.metadataDirectoryName}/${strings.metadataExtensionDirectoryName}uistbdstacbybu/${TestExtension.name}/${TestLogEntityReferenceType.identifier}",
            1
        )
    }

    @Test
    fun testInvalidExtensionPath() {
        testInvalidPath(
            "${strings.metadataDirectoryName}/${strings.metadataExtensionDirectoryName}/${TestExtension.name}uistbdstacbybu/${TestLogEntityReferenceType.identifier}",
            2
        )
    }

    @Test
    fun testInvalidReferenceTypePath() {
        testInvalidPath(
            "${strings.metadataDirectoryName}/${strings.metadataExtensionDirectoryName}/${TestExtension.name}/${TestLogEntityReferenceType.identifier}uistbdstacbybu",
            3
        )
    }

    @Test
    fun testInvalidReferencePath() {
        testInvalidPath(
            "${strings.metadataDirectoryName}/${strings.metadataExtensionDirectoryName}/${TestExtension.name}/${TestLogEntityReferenceType.identifier}",
            4
        )
    }

    private fun testInvalidPath(path: String, firstInvalidIndex: Int) {
        val alerts: MutableList<LogParseAlert> = mutableListOf()
        val result: LogEntityReference? = referenceParser.parseReference(path) { alerts.add(it) }

        assertThat(result).isNull()
        assertThat(alerts).isEqualTo(
            listOf(LogParseAlert.UnknownReferenceType(path.split("/"), firstInvalidIndex))
        )
    }
}
