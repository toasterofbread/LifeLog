package dev.toastbits.lifelog.specification.test

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import dev.toastbits.lifelog.extension.media.MediaExtension
import dev.toastbits.lifelog.extension.media.impl.model.reference.BookMediaReference
import dev.toastbits.lifelog.extension.media.impl.model.reference.GameMediaReference
import dev.toastbits.lifelog.extension.media.impl.model.reference.MovieOrShowMediaReference
import dev.toastbits.lifelog.extension.media.impl.model.reference.SongMediaReference
import dev.toastbits.lifelog.extension.media.model.entity.event.BookMediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.entity.event.GameMediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.entity.event.MovieOrShowMediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.entity.event.SongMediaConsumeEvent
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
        val testReference: MovieOrShowMediaReference = MovieOrShowMediaReference("Show name")

        val dayContent: String = "Gay people stay winning [Test!](/media/movie/${testReference.mediaId})"
        val eventReference: String = "転生王女と天才令嬢の魔法革命"

        val text: String = """
----- 02 July 2024

Watched $eventReference (first watch, eps 1-5) {
    $dayContent
}

Read $eventReference (first watch, eps 1-5) {
    $dayContent
}

Played $eventReference (first watch, eps 1-5) {
    $dayContent
}

Listened to $eventReference (first watch, eps 1-5) {
    $dayContent
}
        """

        val expectedEvents: List<LogEvent> =
            listOf(
                MovieOrShowMediaConsumeEvent(
                    MovieOrShowMediaReference(eventReference),
                    content = dayContent.parse()
                ),
                BookMediaConsumeEvent(
                    BookMediaReference(eventReference),
                    content = dayContent.parse()
                ),
                GameMediaConsumeEvent(
                    GameMediaReference(eventReference),
                    content = dayContent.parse()
                ),
                SongMediaConsumeEvent(
                    SongMediaReference(eventReference),
                    content = dayContent.parse()
                )
            )

        val result: LogDatabaseConverter.ParseResult = parser.parseLogDatabase(text.split('\n'))
        assertThat(result.alerts).isEmpty()

        val database: LogDatabase = result.database
        assertThat(database.days).hasSize(1)

        val day: List<LogEvent>? = database.days[LogDateImpl(LocalDate.parse("2024-07-02"))]
        assertThat(day).isEqualTo(expectedEvents)
    }
}
