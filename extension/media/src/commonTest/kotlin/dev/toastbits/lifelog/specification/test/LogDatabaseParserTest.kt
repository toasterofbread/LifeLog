package dev.toastbits.lifelog.specification.test

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import dev.toastbits.lifelog.extension.media.MediaExtension
import dev.toastbits.lifelog.specification.converter.LogDatabaseConverter
import dev.toastbits.lifelog.specification.database.LogDatabase
import dev.toastbits.lifelog.specification.impl.converter.LogDatabaseConverterImpl
import kotlin.test.BeforeTest
import kotlin.test.Test

class LogDatabaseParserTest {
    private lateinit var parser: LogDatabaseConverter

    @BeforeTest
    fun setUp() {
        parser = LogDatabaseConverterImpl()
        parser.registerExtension(MediaExtension())
    }

    @Test
    fun test() {
        val text: String = """
----- 02 July 2024

Watched 転生王女と天才令嬢の魔法革命 (first watch, eps 1-5) {
    Gay people stay winning [Test!](/media/movie/転生王女と天才令嬢の魔法革命)
}

----- 04 August 2024

Watched 転生王女と天才令嬢の魔法革命 (first watch, eps 6-12) {
    
}
        """

        val result: LogDatabaseConverter.ParseResult = parser.parseLogDatabase(text.split('\n'))
        assertThat(result.alerts).isEmpty()

        val database: LogDatabase = result.database
        assertThat(database.days).hasSize(2)

        println(database.days.toMap())
    }
}
