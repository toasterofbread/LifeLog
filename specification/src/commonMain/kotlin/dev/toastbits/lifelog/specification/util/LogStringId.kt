package dev.toastbits.lifelog.specification.util

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
}
