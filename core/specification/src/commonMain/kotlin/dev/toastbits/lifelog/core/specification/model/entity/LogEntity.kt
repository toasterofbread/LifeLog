package dev.toastbits.lifelog.core.specification.model.entity

import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.util.LogStringId
import dev.toastbits.lifelog.core.specification.util.StringId

// An entity is anything that can be referenced in user content
interface LogEntity {
    var inlineComment: UserContent?
    var aboveComment: UserContent?

    fun getCompanion(): LogEntityCompanion = Companion

    data class Property(
        val name: StringId,
        val accessor: LogEntity.() -> Any?
    )

    companion object: LogEntityCompanion(null) {
        override fun getAllProperties(): List<Property> =
            listOf(
                LogStringId.Property.LogEntity.COMMENT.property<LogEntity> { inlineComment }
            )
    }
}

abstract class LogEntityCompanion(vararg val parents: LogEntityCompanion?) {
    abstract fun getAllProperties(): List<LogEntity.Property>

    companion object {
        fun <T: LogEntity> StringId.property(accessor: T.() -> Any?) =
            LogEntity.Property(this) { accessor(this as T) }
    }
}
