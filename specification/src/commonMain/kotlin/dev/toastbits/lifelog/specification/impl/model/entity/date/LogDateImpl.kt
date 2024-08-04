package dev.toastbits.lifelog.specification.impl.model.entity.date

import dev.toastbits.lifelog.specification.model.UserContent
import dev.toastbits.lifelog.specification.model.entity.LogEntity
import dev.toastbits.lifelog.specification.model.entity.date.LogDate
import kotlinx.datetime.LocalDate

class LogDateImpl(
    initialDate: LocalDate,
    initialComments: List<UserContent> = emptyList()
): LogDate {
    override val date: LogDate.DateProperty = LogDate.DateProperty(initialDate)
    override val comments: LogEntity.CommentsProperty = LogEntity.CommentsProperty(initialComments)

    override fun toString(): String =
        "LogDateImpl(date=${date.value})"
}