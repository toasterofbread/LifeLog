package dev.toastbits.lifelog.core.specification.converter.alert

import dev.toastbits.lifelog.core.specification.converter.alert.LogConvertAlert.Severity

interface LogParseAlert: LogConvertAlert {
    interface Error: LogParseAlert {
        override val severity: Severity get() = Severity.ERROR
    }

    interface Warning: LogParseAlert {
        override val severity: Severity get() = Severity.WARNING
    }
}
