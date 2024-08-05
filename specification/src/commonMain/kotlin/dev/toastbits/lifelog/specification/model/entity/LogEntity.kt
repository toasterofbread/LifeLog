package dev.toastbits.lifelog.specification.model.entity

import dev.toastbits.lifelog.specification.model.UserContent
import dev.toastbits.lifelog.specification.util.LogStringId
import dev.toastbits.lifelog.specification.util.StringId
import kotlin.reflect.KClass

// An entity is anything that can be referenced in user content
interface LogEntity {
    var comments: List<UserContent>

    fun getCompanion(): LogEntityCompanion<*> = Companion

    data class Property<T: LogEntity, V>(
        val name: StringId,
        val accessor: T.() -> V
    )

    companion object: LogEntityCompanion<LogEntity>(null) {
        override fun getAllProperties(): List<Property<*, *>> =
            listOf(
                LogStringId.Property.LogEntity.COMMENT.property { comments }
            )
    }
}

abstract class LogEntityCompanion<T: LogEntity>(vararg val parents: LogEntityCompanion<*>?) {
    abstract fun getAllProperties(): List<LogEntity.Property<*, *>>

    fun <V> StringId.property(accessor: T.() -> V) =
        LogEntity.Property(this, accessor)
}
