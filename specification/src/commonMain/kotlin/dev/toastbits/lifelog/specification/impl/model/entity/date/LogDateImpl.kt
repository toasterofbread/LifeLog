package dev.toastbits.lifelog.specification.impl.model.entity.date

import dev.toastbits.lifelog.specification.model.UserContent
import dev.toastbits.lifelog.specification.model.entity.LogEntity
import dev.toastbits.lifelog.specification.model.entity.date.LogDate
import kotlinx.datetime.LocalDate

data class LogDateImpl(
    override var date: LocalDate,
    override var comments: List<UserContent> = emptyList()
): LogDate
