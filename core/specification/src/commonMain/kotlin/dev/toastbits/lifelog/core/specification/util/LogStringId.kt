package dev.toastbits.lifelog.core.specification.util

sealed interface LogStringId: StringId {
    sealed interface Property: LogStringId {
        enum class LogEntity: Property {
            COMMENT
        }
        enum class LogEvent: Property {
            CONTENT
        }
        enum class LogDate: Property {
            DATE
        }
    }
    enum class EventType: StringId {
        COMMENT
    }
}
