package dev.toastbits.lifelog.specification.model.entity.event

import dev.toastbits.lifelog.specification.model.UserContent
import dev.toastbits.lifelog.specification.model.entity.LogEntity
import dev.toastbits.lifelog.specification.model.entity.LogEntityProperty
import dev.toastbits.lifelog.specification.util.LogStringId

interface LogEvent: LogEntity {
    val content: ContentProperty

    override fun getAllProperties(): List<LogEntityProperty<*, *>> =
        super.getAllProperties() + listOf(content)

    class ContentProperty(override var value: UserContent?) : LogEntityProperty<UserContent?, LogStringId> {
        override val name: LogStringId get() = LogStringId.Property.LogEvent.CONTENT
    }
}
