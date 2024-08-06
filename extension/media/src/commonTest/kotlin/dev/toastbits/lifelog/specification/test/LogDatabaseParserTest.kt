package dev.toastbits.lifelog.specification.test

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import dev.toastbits.lifelog.extension.media.MediaExtension
import dev.toastbits.lifelog.extension.media.impl.model.reference.MediaReferenceImpl
import dev.toastbits.lifelog.extension.media.model.entity.event.MovieOrShowMediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.reference.MediaReferenceType
import dev.toastbits.lifelog.extension.media.util.MediaEntityType
import dev.toastbits.lifelog.specification.converter.LogDatabaseConverter
import dev.toastbits.lifelog.specification.database.LogDatabase
import dev.toastbits.lifelog.specification.impl.converter.LogDatabaseConverterImpl
import dev.toastbits.lifelog.specification.impl.converter.usercontent.MarkdownUserContentParser
import dev.toastbits.lifelog.specification.impl.model.entity.date.LogDateImpl
import dev.toastbits.lifelog.specification.impl.model.reference.LogEntityReferenceParserImpl
import dev.toastbits.lifelog.specification.model.UserContent
import dev.toastbits.lifelog.specification.model.entity.event.LogEvent
import dev.toastbits.lifelog.specification.model.reference.LogEntityReferenceParser
import kotlinx.datetime.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test

class LogDatabaseParserTest {
    private lateinit var parser: LogDatabaseConverter
    private lateinit var markdownParser: MarkdownUserContentParser
    private lateinit var referenceParser: LogEntityReferenceParser

    private fun String.parse(): UserContent =
        markdownParser.parseUserContent(this, referenceParser) { alert, _ -> assertThat(alert).isNull() }

    @BeforeTest
    fun setUp() {
        parser = LogDatabaseConverterImpl()
        parser.registerExtension(MediaExtension())

        markdownParser = MarkdownUserContentParser()
        referenceParser = LogEntityReferenceParserImpl(eventTypes = emptyList(), referenceTypes = listOf(
            MediaReferenceType()
        ))
    }

    @Test
    fun test() {
        val testReference: MediaReferenceImpl = MediaReferenceImpl(MediaEntityType.MOVIE_OR_SHOW, "Show name")

        val day1Content: String = "Gay people stay winning [Test!](/media/movie/${testReference.mediaId})"
        val day1Reference: String = "転生王女と天才令嬢の魔法革命"

        val text: String = """
----- 02 July 2024

Watched $day1Reference (first watch, eps 1-5) {
    $day1Content
}

----- 04 August 2024

Watched 転生王女と天才令嬢の魔法革命 (first watch, eps 6-12) {
    
}
        """

        val result: LogDatabaseConverter.ParseResult = parser.parseLogDatabase(text.split('\n'))
        assertThat(result.alerts).isEmpty()

        val database: LogDatabase = result.database
        assertThat(database.days).hasSize(2)

        val day1: List<LogEvent>? = database.days[LogDateImpl(LocalDate.parse("2024-07-02"))]
        val day2: List<LogEvent>? = database.days[LogDateImpl(LocalDate.parse("2024-08-04"))]

        assertThat(day1).isNotNull()
        assertThat(day1!!).hasSize(1)
        assertThat(day1.single()).isEqualTo(
            MovieOrShowMediaConsumeEvent(
                MediaReferenceImpl(MediaEntityType.MOVIE_OR_SHOW, day1Reference),
                content = day1Content.parse()
            )
        )

        assertThat(day2).isNotNull()
        assertThat(day2!!).hasSize(1)
        assertThat(day2.single()).isEqualTo(
            MovieOrShowMediaConsumeEvent(
                MediaReferenceImpl(MediaEntityType.MOVIE_OR_SHOW, day1Reference),
                content = UserContent(emptyList())
            )
        )
    }
}
