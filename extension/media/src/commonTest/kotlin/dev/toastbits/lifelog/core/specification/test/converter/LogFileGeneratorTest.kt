package dev.toastbits.lifelog.core.specification.test.converter

import assertk.assertThat
import assertk.assertions.isEmpty
import dev.toastbits.lifelog.core.specification.converter.LogFileConverter
import dev.toastbits.lifelog.core.specification.impl.converter.LogFileConverterImpl
import dev.toastbits.lifelog.core.specification.impl.converter.usercontent.MarkdownUserContentGenerator
import dev.toastbits.lifelog.core.specification.impl.model.entity.date.LogDateImpl
import dev.toastbits.lifelog.core.specification.impl.model.reference.LogEntityReferenceGeneratorImpl
import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceGenerator
import dev.toastbits.lifelog.extension.media.MediaExtension
import dev.toastbits.lifelog.extension.media.impl.model.entity.event.MediaConsumeEventTypeImpl
import dev.toastbits.lifelog.extension.media.impl.model.reference.MovieOrShowMediaReference
import dev.toastbits.lifelog.extension.media.model.entity.event.MovieOrShowMediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.reference.MediaReferenceType
import kotlinx.datetime.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test

class LogFileGeneratorTest {
    private lateinit var converter: LogFileConverter
    private lateinit var markdownGenerator: MarkdownUserContentGenerator
    private lateinit var referenceGenerator: LogEntityReferenceGenerator

    @BeforeTest
    fun setUp() {
        converter = LogFileConverterImpl()
        converter.registerExtension(MediaExtension())

        markdownGenerator = MarkdownUserContentGenerator()
        referenceGenerator = LogEntityReferenceGeneratorImpl(referenceTypes = listOf(
            MediaReferenceType()
        ))
    }

    @Test
    fun temp() {
        val days: Map<LogDate, List<LogEvent>> =
            mapOf(
                LogDateImpl(LocalDate.parse("2024-08-04")) to listOf(
                    MovieOrShowMediaConsumeEvent(MovieOrShowMediaReference("test 2"), iteration = 1)
                )
            )

        val result: LogFileConverter.GenerateResult = converter.generateLogFile(days)
        assertThat(result.alerts).isEmpty()
    }
}
