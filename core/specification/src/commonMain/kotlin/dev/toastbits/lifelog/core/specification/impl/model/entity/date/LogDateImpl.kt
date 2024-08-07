package dev.toastbits.lifelog.core.specification.impl.model.entity.date

import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.model.entity.LogEntity
import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate
import kotlinx.datetime.LocalDate

class LogDateImpl(
    override var date: LocalDate,
    override var comments: List<UserContent> = emptyList()
): LogDate {
    override fun equals(other: Any?): Boolean =
        other is LogDate && date == other.date

    override fun hashCode(): Int =
        date.hashCode()
}
