package dev.toastbits.lifelog.specification.impl.model.entity.event

import dev.toastbits.lifelog.specification.model.UserContent
import dev.toastbits.lifelog.specification.model.entity.LogEntity
import dev.toastbits.lifelog.specification.model.entity.event.LogComment
import dev.toastbits.lifelog.specification.model.entity.event.LogEvent

class LogCommentImpl(initialComment: UserContent?): LogComment {
    override val content: LogEvent.ContentProperty = LogEvent.ContentProperty(initialComment)
    override val comments: LogEntity.CommentsProperty = LogEntity.CommentsProperty(emptyList())
}
