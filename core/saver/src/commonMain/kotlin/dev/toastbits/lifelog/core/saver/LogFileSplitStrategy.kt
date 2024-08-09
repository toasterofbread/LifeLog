package dev.toastbits.lifelog.core.saver

import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate
import kotlinx.datetime.LocalDate

sealed interface LogFileSplitStrategy {
    fun getDateComponents(date: LocalDate): List<Int>

    data object Year: LogFileSplitStrategy {
        override fun getDateComponents(date: LocalDate): List<Int> =
            listOf(date.year)
    }

    data object Month: LogFileSplitStrategy {
        override fun getDateComponents(date: LocalDate): List<Int> =
            listOf(date.year, date.monthNumber)
    }

    data object Day: LogFileSplitStrategy {
        override fun getDateComponents(date: LocalDate): List<Int> =
            listOf(date.year, date.monthNumber, date.dayOfMonth)
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
