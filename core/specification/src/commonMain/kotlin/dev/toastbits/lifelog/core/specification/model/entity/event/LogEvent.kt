package dev.toastbits.lifelog.core.specification.model.entity.event

import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.model.entity.LogEntity
import dev.toastbits.lifelog.core.specification.model.entity.LogEntity.Property
import dev.toastbits.lifelog.core.specification.model.entity.LogEntityCompanion
import dev.toastbits.lifelog.core.specification.model.entity.LogEntityProperty
import dev.toastbits.lifelog.core.specification.model.entity.LogEntityPropertyData
import dev.toastbits.lifelog.core.specification.util.LogStringId

interface LogEvent: LogEntity {
    var content: UserContent?

    override fun getCompanion(): LogEntityCompanion<*> = Companion

    companion object: LogEntityCompanion<LogEvent>(LogEntity) {
        override fun getAllProperties(): List<Property<*, *>> =
            listOf(
                LogStringId.Property.LogEvent.CONTENT.property { content }
            )
    }
}
