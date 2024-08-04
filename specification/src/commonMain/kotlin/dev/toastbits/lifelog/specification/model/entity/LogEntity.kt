package dev.toastbits.lifelog.specification.model.entity

import dev.toastbits.lifelog.specification.model.UserContent
import dev.toastbits.lifelog.specification.util.LogStringId

// An entity is anything that can be referenced in user content
interface LogEntity {
    val comments: CommentsProperty

    fun getAllProperties(): List<LogEntityProperty<*, *>> = listOf(comments)

    class CommentsProperty(override var value: List<UserContent>): LogEntityProperty<List<UserContent>, LogStringId> {
        override val name: LogStringId get() = LogStringId.Property.LogEntity.COMMENT
    }
}
