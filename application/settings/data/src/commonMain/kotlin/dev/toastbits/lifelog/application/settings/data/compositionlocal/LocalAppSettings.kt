package dev.toastbits.lifelog.application.settings.data.compositionlocal

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import dev.toastbits.lifelog.application.settings.domain.appsettings.AppSettings

val LocalSettings: ProvidableCompositionLocal<AppSettings> =
    staticCompositionLocalOf { throw IllegalStateException() }
