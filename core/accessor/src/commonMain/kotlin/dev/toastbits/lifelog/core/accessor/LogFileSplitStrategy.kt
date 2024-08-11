package dev.toastbits.lifelog.core.accessor

import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate
import kotlinx.datetime.LocalDate

sealed interface LogFileSplitStrategy {
    val componentsCount: Int
    fun getDateComponents(date: LocalDate): List<Int>
    fun parseDateComponents(components: List<Int>): LocalDate

    data object Year: LogFileSplitStrategy {
        override val componentsCount: Int = 1
        override fun getDateComponents(date: LocalDate): List<Int> =
            listOf(date.year)

        override fun parseDateComponents(components: List<Int>): LocalDate {
            require(components.size == componentsCount)
            return LocalDate(components[0], 1, 1)
        }
    }

    data object Month: LogFileSplitStrategy {
        override val componentsCount: Int = 2
        override fun getDateComponents(date: LocalDate): List<Int> =
            listOf(date.year, date.monthNumber)
        override fun parseDateComponents(components: List<Int>): LocalDate {
            require(components.size == componentsCount)
            return LocalDate(components[0], components[1], 1)
        }
    }

    data object Day: LogFileSplitStrategy {
        override val componentsCount: Int = 3
        override fun getDateComponents(date: LocalDate): List<Int> =
            listOf(date.year, date.monthNumber, date.dayOfMonth)
        override fun parseDateComponents(components: List<Int>): LocalDate {
            require(components.size == componentsCount)
            return LocalDate(components[0], components[1], components[2])
        }
    }
}

fun LogFileSplitStrategy.splitDaysIntoGroups(days: Iterable<LogDate>): List<List<LogDate>> =
    days.splitBy { getDateComponents(this) }

private fun <T> Iterable<LogDate>.splitBy(getValue: LocalDate.() -> T): List<List<LogDate>> {
    val groups: MutableMap<T, MutableList<LogDate>> = mutableMapOf()
    for (day in this) {
        val group: MutableList<LogDate> = groups.getOrPut(getValue(day.date)) { mutableListOf() }
        group.add(day)
    }
    return groups.values.toList()
}
