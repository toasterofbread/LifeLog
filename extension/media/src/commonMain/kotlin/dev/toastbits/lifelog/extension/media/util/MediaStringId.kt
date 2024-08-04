package dev.toastbits.lifelog.extension.media.util

import dev.toastbits.lifelog.specification.util.StringId

sealed interface MediaStringId: StringId {
    sealed interface Property: MediaStringId {
        enum class MediaConsumeEvent: Property {
            MEDIA_REFERENCE
        }
    }

    enum class MediaExtension: MediaStringId {
        NAME
    }

    enum class MediaReferenceType: MediaStringId {
        NAME
    }
}
