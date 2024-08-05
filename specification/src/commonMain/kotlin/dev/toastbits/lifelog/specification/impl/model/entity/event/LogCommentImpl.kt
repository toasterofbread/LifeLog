package dev.toastbits.lifelog.specification.impl.model.entity.event

import dev.toastbits.lifelog.specification.model.UserContent
import dev.toastbits.lifelog.specification.model.entity.LogEntity
import dev.toastbits.lifelog.specification.model.entity.LogEntityCompanion
import dev.toastbits.lifelog.specification.model.entity.event.LogComment
import dev.toastbits.lifelog.specification.model.entity.event.LogEvent

class LogCommentImpl(
    override var content: UserContent?,
    override var comments: List<UserContent> = emptyList()
): LogComment
