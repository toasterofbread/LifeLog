package dev.toastbits.lifelog.specification.model.entity.date

import dev.toastbits.lifelog.specification.model.entity.LogEntity
import dev.toastbits.lifelog.specification.model.entity.LogEntityProperty
import dev.toastbits.lifelog.specification.util.LogStringId
import kotlinx.datetime.LocalDate

interface LogDate: LogEntity {
    val date: DateProperty

    override fun getAllProperties(): List<LogEntityProperty<*, *>> =
        super.getAllProperties() + listOf(date)

    open class DateProperty(override var value: LocalDate): LogEntityProperty<LocalDate, LogStringId> {
        override val name: LogStringId get() = LogStringId.Property.LogDate.DATE
    }
}
