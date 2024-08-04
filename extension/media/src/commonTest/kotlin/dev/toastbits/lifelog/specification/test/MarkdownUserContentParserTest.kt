package dev.toastbits.lifelog.specification.test

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import dev.toastbits.lifelog.extension.media.impl.model.reference.MediaReferenceImpl
import dev.toastbits.lifelog.extension.media.model.reference.MediaReferenceType
import dev.toastbits.lifelog.extension.media.util.MediaEntityType
import dev.toastbits.lifelog.specification.impl.converter.usercontent.MarkdownUserContentParser
import dev.toastbits.lifelog.specification.impl.model.reference.LogEntityReferenceParserImpl
import dev.toastbits.lifelog.specification.model.UserContent
import dev.toastbits.lifelog.specification.model.reference.LogEntityReferenceParser
import kotlin.test.BeforeTest
import kotlin.test.Test

class MarkdownUserContentParserTest {
    private lateinit var parser: MarkdownUserContentParser
    private lateinit var referenceParser: LogEntityReferenceParser
    private val testReference: MediaReferenceImpl = MediaReferenceImpl(MediaEntityType.MOVIE, "転生王女と天才令嬢の魔法革命")

    @BeforeTest
    fun setUp() {
        parser = MarkdownUserContentParser()
        referenceParser = LogEntityReferenceParserImpl(eventTypes = emptyList(), referenceTypes = listOf(MediaReferenceType()))
    }

    @Test
    fun testValidMarkdown() {
        val text: String = """
----- 02 July 2024

Watched 転生王女と天才令嬢の魔法革命 (first watch, eps 1-5) {
    Gay people stay winning [Test!](/media/${testReference.mediaType.name}/${testReference.mediaId})
}

----- 04 August 2024

Watched 転生王女と天才令嬢の魔法革命 (first watch, eps 6-12) {
}
        """

        val renderedText: String = """
----- 02 July 2024

Watched 転生王女と天才令嬢の魔法革命 (first watch, eps 1-5) {
    Gay people stay winning Test!
}

----- 04 August 2024

Watched 転生王女と天才令嬢の魔法革命 (first watch, eps 6-12) {
}
        """

        val parsed: UserContent =
            parser.parseUserContent(text, referenceParser) { alert -> assertThat(alert).isNull() }

        assertThat(parsed.asText()).isEqualTo(renderedText)
        assertThat(parsed).isEqualTo(parsed.normalised())

        assertThat(parsed.parts).hasSize(3)

        assertThat(parsed.parts[0].modifiers).isEmpty()
        assertThat(parsed.parts[2].modifiers).isEmpty()

        assertThat(parsed.parts[1].modifiers).hasSize(1)
        assertThat(parsed.parts[1].modifiers.single()).isEqualTo(UserContent.Modifier.Reference(testReference))
    }
}
