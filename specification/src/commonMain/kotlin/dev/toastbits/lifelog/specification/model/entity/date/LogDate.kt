package dev.toastbits.lifelog.specification.model.entity.date

import dev.toastbits.lifelog.specification.model.entity.LogEntity
import dev.toastbits.lifelog.specification.model.entity.LogEntityCompanion
import dev.toastbits.lifelog.specification.model.entity.LogEntityProperty
import dev.toastbits.lifelog.specification.util.LogStringId
import kotlinx.datetime.LocalDate

interface LogDate: LogEntity {
    var date: LocalDate

    override fun getCompanion(): LogEntityCompanion<*> = Companion

    companion object: LogEntityCompanion<LogDate>(LogEntity) {
        override fun getAllProperties(): List<LogEntity.Property<*, *>> =
            listOf(
                LogStringId.Property.LogDate.DATE.property { date }
            )
    }
}
