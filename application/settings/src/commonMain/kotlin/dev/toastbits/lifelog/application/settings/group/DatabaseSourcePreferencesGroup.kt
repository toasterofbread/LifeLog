package dev.toastbits.lifelog.application.settings.group

import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.platform.PreferencesGroup
import dev.toastbits.composekit.platform.PreferencesProperty
import dev.toastbits.lifelog.application.dbsource.source.configuration.DatabaseSourceConfiguration
import lifelog.application.settings.generated.resources.Res
import lifelog.application.settings.generated.resources.pref_database_source_database_sources_description
import lifelog.application.settings.generated.resources.pref_database_source_database_sources_title
import org.jetbrains.compose.resources.stringResource

@Suppress("PropertyName")
class DatabaseSourcePreferencesGroup(preferences: PlatformPreferences): PreferencesGroup("DATABASE_SOURCE", preferences) {
    val AUTO_OPEN_SOURCE_INDEX: PreferencesProperty<Int> by property(
        getName = { throw IllegalStateException("Internal property") },
        getDescription = { throw IllegalStateException("Internal property") },
        getDefaultValue = { -1 }
    )

    val DATABASE_SOURCES: PreferencesProperty<List<DatabaseSourceConfiguration>> by
        serialisableProperty(
            getName = { stringResource(Res.string.pref_database_source_database_sources_title) },
            getDescription = { stringResource(Res.string.pref_database_source_database_sources_description) },
            getDefaultValue = { emptyList() }
        )
}
