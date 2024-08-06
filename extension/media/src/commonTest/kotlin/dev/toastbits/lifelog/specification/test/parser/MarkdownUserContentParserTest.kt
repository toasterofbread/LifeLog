package dev.toastbits.lifelog.specification.test.parser

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import dev.toastbits.lifelog.extension.media.impl.model.reference.MovieOrShowMediaReference
import dev.toastbits.lifelog.extension.media.model.reference.MediaReferenceType
import dev.toastbits.lifelog.specification.impl.converter.usercontent.MarkdownUserContentParser
import dev.toastbits.lifelog.specification.impl.model.reference.LogEntityReferenceParserImpl
import dev.toastbits.lifelog.specification.model.UserContent
import dev.toastbits.lifelog.specification.model.reference.LogEntityReferenceParser
import dev.toastbits.lifelog.specification.testutil.parser.ParserTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class MarkdownUserContentParserTest: ParserTest() {
    @Test
    fun testEntityReference() {
        val testReference: MovieOrShowMediaReference = MovieOrShowMediaReference("転生王女と天才令嬢の魔法革命")

        val text: String = "Hello [World!](/media/movie/${testReference.mediaId})".inTemplate()
        val renderedText: String = "Hello World!".inTemplate()

        val parsed: UserContent = parseAndTest(text, renderedText)

        assertThat(parsed.parts).hasSize(3)

        assertThat(parsed.parts[0].modifiers).isEmpty()
        assertThat(parsed.parts[2].modifiers).isEmpty()

        assertThat(parsed.parts[1].modifiers).hasSize(1)
        assertThat(parsed.parts[1].modifiers.single()).isEqualTo(UserContent.Modifier.Reference(testReference))
    }

    @Test
    fun testTextFormatting() {
        val text: String = "Normal *Italic* **Bold** ***Both*** `Code`"
        val renderedText: String = "Normal Italic Bold Both Code"

        val expectedParts: List<UserContent.Part.Single> =
            listOf(
                UserContent.Part.Single("Normal "),
                UserContent.Part.Single("Italic", setOf(UserContent.Modifier.Italic)),
                UserContent.Part.Single(" "),
                UserContent.Part.Single("Bold", setOf(UserContent.Modifier.Bold)),
                UserContent.Part.Single(" "),
                UserContent.Part.Single("Both", setOf(UserContent.Modifier.Bold, UserContent.Modifier.Italic)),
                UserContent.Part.Single(" "),
                UserContent.Part.Single("Code", setOf(UserContent.Modifier.Code)),
            )

        val parsed: UserContent = parseAndTest(text, renderedText)
        assertThat(parsed.parts).isEqualTo(expectedParts)
    }

    @Test
    fun testCodeBlock() {
        val text: String = "```\nThis is a code block\n```"
        val renderedText: String = "This is a code block"

        val expectedParts: List<UserContent.Part.Single> =
            listOf(
                UserContent.Part.Single("This is a code block", setOf(UserContent.Modifier.CodeBlock))
            )

        val parsed: UserContent = parseAndTest(text, renderedText)
        assertThat(parsed.parts).isEqualTo(expectedParts)
    }
}