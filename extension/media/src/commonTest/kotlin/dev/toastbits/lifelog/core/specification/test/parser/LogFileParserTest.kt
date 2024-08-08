package dev.toastbits.lifelog.core.specification.test.parser

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import dev.toastbits.lifelog.core.specification.converter.LogFileConverter
import dev.toastbits.lifelog.extension.media.impl.model.reference.BookMediaReference
import dev.toastbits.lifelog.extension.media.impl.model.reference.GameMediaReference
import dev.toastbits.lifelog.extension.media.impl.model.reference.MovieOrShowMediaReference
import dev.toastbits.lifelog.extension.media.impl.model.reference.SongMediaReference
import dev.toastbits.lifelog.extension.media.model.entity.event.BookMediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.entity.event.GameMediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.entity.event.MediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.entity.event.MovieOrShowMediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.entity.event.SongMediaConsumeEvent
import dev.toastbits.lifelog.extension.media.util.MediaEntityType
import dev.toastbits.lifelog.core.specification.impl.model.entity.date.LogDateImpl
import dev.toastbits.lifelog.core.specification.impl.model.entity.event.LogCommentImpl
import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent
import dev.toastbits.lifelog.core.specification.testutil.parser.ParserTest
import kotlinx.datetime.LocalDate.Formats.ISO
import kotlinx.datetime.format
import kotlin.test.Test

class LogFileParserTest: ParserTest() {

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
                        ----- ${templateDate.format(ISO)}
                        ${prefix.uppercase()} Test Test Test ($iterationText $suffix)
                        """.trimIndent()

                        val result: LogFileConverter.ParseResult = parser.parseLogFile(text.split('\n'))
                        assertThat(result.alerts).isEmpty()

                        val event: LogEvent = result.days[LogDateImpl(templateDate)]!!.single()
                        assertThat(event).isInstanceOf<MediaConsumeEvent>()

                        assertThat((event as MediaConsumeEvent).iteration).isEqualTo(iterationNumber)
                    }
                }
            }
        }
    }

    @Test
    fun testComments() {

        val text: String = """
// Date comment
----- ${templateDate.format(ISO)} // Inline date comment

// Standalone comment

// Event comment
Watched Test Test Test (first watch, eps 1-5) { // Inline event comment
    
}

// Standalone comment
        """

        val result: LogFileConverter.ParseResult = parser.parseLogFile(text.split('\n'))
        assertThat(result.alerts).isEmpty()

        val (date: LogDate?, day: List<LogEvent>) = result.days.entries.single()
        assertThat(date?.date).isEqualTo(templateDate)
        assertThat(date?.comments).isEqualTo(
            listOf(
                UserContent.single("Date comment"), UserContent.single("Inline date comment")
            )
        )

        assertThat(day).hasSize(3)

        assertThat(day[0]).isEqualTo(
            LogCommentImpl(UserContent.single("Standalone comment"))
        )
        assertThat(day[1]).isEqualTo(
            MovieOrShowMediaConsumeEvent(
                MovieOrShowMediaReference("Test Test Test"),
                comments = listOf(UserContent.single("Event comment"), UserContent.single("Inline event comment"))
            )
        )
        assertThat(day[2]).isEqualTo(
            LogCommentImpl(UserContent.single("Standalone comment"))
        )

    }

    @Test
    fun test() {
        val testReference: MovieOrShowMediaReference = MovieOrShowMediaReference("Show name")

        val dayContent: String = "Gay people stay winning [Test!](/media/movie/${testReference.mediaId})"
        val eventReference: String = "転生王女と天才令嬢の魔法革命"

        val text: String = """
----- ${templateDate.format(ISO)}

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

        val result: LogFileConverter.ParseResult = parser.parseLogFile(text.split('\n'))
        assertThat(result.alerts).isEmpty()

        assertThat(result.days).hasSize(1)

        val day: List<LogEvent>? = result.days[LogDateImpl(templateDate)]
        assertThat(day).isEqualTo(expectedEvents)
    }
}
