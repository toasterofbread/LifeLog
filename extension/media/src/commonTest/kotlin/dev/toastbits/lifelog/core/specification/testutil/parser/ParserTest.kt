package dev.toastbits.lifelog.core.specification.testutil.parser

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import dev.toastbits.lifelog.extension.media.MediaExtension
import dev.toastbits.lifelog.core.specification.converter.LogFileConverter
import dev.toastbits.lifelog.core.specification.impl.converter.LogFileConverterImpl
import dev.toastbits.lifelog.core.specification.impl.converter.usercontent.MarkdownUserContentParser
import dev.toastbits.lifelog.core.specification.impl.model.reference.LogEntityReferenceParserImpl
import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceParser
import kotlinx.datetime.LocalDate
import kotlin.test.BeforeTest

open class ParserTest {
    lateinit var converter: LogFileConverter
        private set
    lateinit var markdownParser: MarkdownUserContentParser
        private set
    lateinit var referenceParser: LogEntityReferenceParser
        private set

    val mediaExtension: MediaExtension = MediaExtension()

    @BeforeTest
    fun setUp() {
        converter = LogFileConverterImpl()
        converter.registerExtension(mediaExtension)

        markdownParser = MarkdownUserContentParser()
        referenceParser = LogEntityReferenceParserImpl(eventTypes = emptyList(), referenceTypes = listOf(
            MediaReferenceType()
        ))
    }

    val templateDate: LocalDate = LocalDate.parse("2024-07-02")

    fun String.inTemplate(): String =
        """
----- 02 July 2024
Watched 転生王女と天才令嬢の魔法革命 (first watch, eps 1-5) {
    $this
}
        """

    fun String.parse(): UserContent =
        markdownParser.parseUserContent(this, referenceParser) { alert, _ -> assertThat(alert).isNull() }

    fun parseAndTest(text: String, renderedText: String): UserContent {
        val parsed: UserContent = text.parse()
        assertThat(parsed.asText()).isEqualTo(renderedText)
        assertThat(parsed).isEqualTo(parsed.normalised())
        return parsed.normalised()
    }
}
