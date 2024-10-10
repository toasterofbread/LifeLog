package dev.toastbits.lifelog.core.specification.impl.model.entity.event

import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.model.entity.event.LogCommentEvent
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent

data class LogCommentEventImpl(
    override var content: UserContent?,
    override var inlineComment: UserContent? = null,
    override var aboveComment: UserContent? = null
): LogCommentEvent {
    override fun getIcon(): LogEvent.Icon = LogEvent.Icon.Chat
}
