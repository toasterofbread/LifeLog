package dev.toastbits.lifelog.core.specification.model.entity.date

import dev.toastbits.lifelog.core.specification.model.entity.LogEntity
import dev.toastbits.lifelog.core.specification.model.entity.LogEntityCompanion
import dev.toastbits.lifelog.core.specification.util.LogStringId
import kotlinx.datetime.LocalDate

interface LogDate: LogEntity {
    var date: LocalDate
    var ambiguous: Boolean

    override fun getCompanion(): LogEntityCompanion<*> = Companion

    companion object: LogEntityCompanion<LogDate>(LogEntity) {
        override fun getAllProperties(): List<LogEntity.Property<*, *>> =
            listOf(
                LogStringId.Property.LogDate.DATE.property { date }
            )
    }
}
