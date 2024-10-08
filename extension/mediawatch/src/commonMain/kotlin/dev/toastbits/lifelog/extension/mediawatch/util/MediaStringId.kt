package dev.toastbits.lifelog.extension.mediawatch.util

import dev.toastbits.lifelog.core.specification.util.StringId

sealed interface MediaStringId: StringId {
    sealed interface Property: MediaStringId {
        enum class MediaEntity: Property {
            ITERATION
        }

        enum class MediaEntityMovieOrShow: Property {
            RUNTIME,
            PART_COUNT
        }

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
