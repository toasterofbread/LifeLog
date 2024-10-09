package dev.toastbits.lifelog.core.specification.util

sealed interface LogStringId: StringId {
    override val id: String get() = ""

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
