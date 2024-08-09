package dev.toastbits.lifelog.core.specification.test.converter

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.toastbits.lifelog.core.specification.converter.LogFileConverter
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterFormats
import dev.toastbits.lifelog.core.specification.impl.converter.LogFileConverterImpl
import dev.toastbits.lifelog.core.specification.impl.converter.LogFileConverterImpl.Companion.DEFAULT_FORMATS
import dev.toastbits.lifelog.core.specification.impl.converter.usercontent.MarkdownUserContentGenerator
import dev.toastbits.lifelog.core.specification.impl.model.entity.date.LogDateImpl
import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityPath
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceGenerator
import dev.toastbits.lifelog.extension.media.MediaExtension
import dev.toastbits.lifelog.extension.media.impl.model.reference.MovieOrShowMediaReference
import dev.toastbits.lifelog.extension.media.model.entity.event.MovieOrShowMediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.reference.MediaReference
import kotlinx.datetime.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test

class LogFileGeneratorTest {
    private lateinit var converter: LogFileConverter
    private lateinit var markdownGenerator: MarkdownUserContentGenerator
    private lateinit var referenceGenerator: LogEntityReferenceGenerator

    private val formats: LogFileConverterFormats = DEFAULT_FORMATS
    private val mediaExtension: MediaExtension = MediaExtension()
    private val mockResultReferencePath: LogEntityPath = LogEntityPath.of("TEST1", "TEST2")

    @BeforeTest
    fun setUp() {
        referenceGenerator = mock {
            every { generateReferencePath(any(), any(), any()) } returns mockResultReferencePath
        }
        converter = LogFileConverterImpl(mock {}, { referenceGenerator }, formats = formats)
        converter.registerExtension(mediaExtension)

        markdownGenerator = MarkdownUserContentGenerator()
    }

    @Test
    fun temp() {
        val date: LocalDate = LocalDate.parse("2024-08-04")
        val mediaReference: MediaReference = MovieOrShowMediaReference("test 2")
        val days: Map<LogDate, List<LogEvent>> =
            mapOf(
                LogDateImpl(date) to listOf(
                    MovieOrShowMediaConsumeEvent(mediaReference, iteration = 1)
                )
            )

        val result: LogFileConverter.GenerateResult = converter.generateLogFile(days)
        assertThat(result.alerts).isEmpty()

        val expectedLines: List<String> =
            listOf(
                formats.datePrefix + formats.preferredDateFormat.format(date),
                "",
                mediaExtension.converterFormats.getMediaEntityTypeConsumeEventPrefixes(mediaReference.mediaType).first() + "[${mediaReference.mediaId}](<$mockResultReferencePath>) (first watch)",
                ""
            )

        assertThat(result.lines).isEqualTo(expectedLines)
    }
}
