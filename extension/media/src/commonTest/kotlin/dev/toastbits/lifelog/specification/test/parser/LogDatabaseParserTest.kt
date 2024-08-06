package dev.toastbits.lifelog.specification.test.parser

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import dev.toastbits.lifelog.extension.media.MediaExtension
import dev.toastbits.lifelog.extension.media.impl.model.reference.BookMediaReference
import dev.toastbits.lifelog.extension.media.impl.model.reference.GameMediaReference
import dev.toastbits.lifelog.extension.media.impl.model.reference.MovieOrShowMediaReference
import dev.toastbits.lifelog.extension.media.impl.model.reference.SongMediaReference
import dev.toastbits.lifelog.extension.media.model.entity.event.BookMediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.entity.event.GameMediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.entity.event.MediaConsumeEvent
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
import dev.toastbits.lifelog.specification.testutil.parser.ParserTest
import kotlinx.datetime.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test

class LogDatabaseParserTest: ParserTest() {

    @Test
    fun testMediaConsumeEventIterations() {
        val numberTypes: Map<String, Int> =
            mapOf(
                "first" to 1, "1st" to 1,
                "second" to 2, "2nd" to 2,
                "third" to 3, "3rd" to 3,
                "fourth" to 4,
                "fifth" to 5,
                "sixth" to 6,
                "seventh" to 7,
                "eighth" to 8,
                "ninth" to 9,
                "tenth" to 10,
            ) + (4..1000).associateBy { "${it}th" }

        for ((iterationText, iterationNumber) in numberTypes) {
            for (entityType in MediaEntityType.entries) {
                for (prefix in mediaExtension.converterFormats.getMediaEntityTypeConsumeEventPrefixes(entityType)) {
                    for (suffix in mediaExtension.converterFormats.getMediaEntityTypeIterationSuffixes(entityType)) {
                        val text: String = """
                        ----- 02 July 2024
                        ${prefix.uppercase()} Test Test Test ($iterationText $suffix)
                        """.trimIndent()

                        val result: LogDatabaseConverter.ParseResult = parser.parseLogDatabase(text.split('\n'))
                        assertThat(result.alerts).isEmpty()

                        val event: LogEvent = result.database.days[LogDateImpl(templateDate)]!!.single()
                        assertThat(event).isInstanceOf<MediaConsumeEvent>()

                        assertThat((event as MediaConsumeEvent).iteration).isEqualTo(iterationNumber)
                    }
                }
            }
        }
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

Read $eventReference (2nd read, pages 1-87) {
    $dayContent
}

Played $eventReference (third play, 4 hours) {
    $dayContent
}

Listened to $eventReference (12th listen) {
    $dayContent
}
        """

        val expectedEvents: List<LogEvent> =
            listOf(
                MovieOrShowMediaConsumeEvent(
                    MovieOrShowMediaReference(eventReference),
                    content = dayContent.parse(),
                    iteration = 1
                ),
                BookMediaConsumeEvent(
                    BookMediaReference(eventReference),
                    content = dayContent.parse(),
                    iteration = 2
                ),
                GameMediaConsumeEvent(
                    GameMediaReference(eventReference),
                    content = dayContent.parse(),
                    iteration = 3
                ),
                SongMediaConsumeEvent(
                    SongMediaReference(eventReference),
                    content = dayContent.parse(),
                    iteration = 12
                )
            )

        val result: LogDatabaseConverter.ParseResult = parser.parseLogDatabase(text.split('\n'))
        assertThat(result.alerts).isEmpty()

        val database: LogDatabase = result.database
        assertThat(database.days).hasSize(1)

        val day: List<LogEvent>? = database.days[LogDateImpl(templateDate)]
        assertThat(day).isEqualTo(expectedEvents)
    }
}
