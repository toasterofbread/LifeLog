package dev.toastbits.lifelog.extension.media.impl.model.entity.event

import dev.toastbits.lifelog.extension.media.model.entity.event.MediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.entity.event.MediaConsumeEventType
import dev.toastbits.lifelog.extension.media.util.MediaStringId
import dev.toastbits.lifelog.specification.model.UserContent

class MediaConsumeEventTypeImpl : MediaConsumeEventType {
    override val name: MediaStringId = MediaStringId.MediaExtension.NAME

    enum class Prefixes {
        WATCHED,
        READ,
        PLAYED,
        LISTENED_TO;
    }

    override val prefixes: List<String> =
        Prefixes.entries.map { it.name }

    override fun parseEvent(
        prefixIndex: Int,
        body: String,
        metadata: String?,
        content: UserContent
    ): MediaConsumeEvent {
        when (Prefixes.entries[prefixIndex]) {
            Prefixes.WATCHED -> TODO()
            Prefixes.READ -> TODO()
            Prefixes.PLAYED -> TODO()
            Prefixes.LISTENED_TO -> TODO()
        }
    }
}
