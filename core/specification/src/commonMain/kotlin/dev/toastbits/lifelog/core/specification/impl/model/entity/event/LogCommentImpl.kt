package dev.toastbits.lifelog.core.specification.impl.model.entity.event

import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.model.entity.LogEntity
import dev.toastbits.lifelog.core.specification.model.entity.LogEntityCompanion
import dev.toastbits.lifelog.core.specification.model.entity.event.LogComment
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent

data class LogCommentImpl(
    override var content: UserContent?,
    override var inlineComment: UserContent? = null,
    override var aboveComment: UserContent? = null
): LogComment
