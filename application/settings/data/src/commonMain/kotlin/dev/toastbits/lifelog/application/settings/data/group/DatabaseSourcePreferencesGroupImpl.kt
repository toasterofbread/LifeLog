package dev.toastbits.lifelog.application.settings.data.group

import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.platform.PreferencesGroupImpl
import dev.toastbits.composekit.platform.PreferencesProperty
import dev.toastbits.lifelog.application.dbsource.domain.type.DatabaseSourceTypeRegistry
import dev.toastbits.lifelog.application.dbsource.inmemorygit.type.InMemoryGitDatabaseSourceType
import dev.toastbits.lifelog.application.settings.domain.group.DatabaseSourcePreferencesGroup
import dev.toastbits.lifelog.application.settings.domain.model.SerialisedDatabaseSourceConfiguration
import lifelog.application.settings.data.generated.resources.Res
import lifelog.application.settings.data.generated.resources.pref_database_source_database_sources_description
import lifelog.application.settings.data.generated.resources.pref_database_source_database_sources_title
import org.jetbrains.compose.resources.stringResource

open class DatabaseSourcePreferencesGroupImpl(preferences: PlatformPreferences): PreferencesGroupImpl("DATABASE_SOURCE", preferences), DatabaseSourcePreferencesGroup {
    override val sourceTypeRegistry: DatabaseSourceTypeRegistry =
        MapDatabaseSourceTypeRegistry(
            mapOf(
                "InMemoryGit" to InMemoryGitDatabaseSourceType
            )
        )

    override val AUTO_OPEN_SOURCE_INDEX: PreferencesProperty<Int> by property(
        getName = { throw IllegalStateException("Internal property") },
        getDescription = { throw IllegalStateException("Internal property") },
        getDefaultValue = { -1 }
    )

    override val DATABASE_SOURCES: PreferencesProperty<List<SerialisedDatabaseSourceConfiguration>> by
        serialisableProperty(
            getName = { stringResource(Res.string.pref_database_source_database_sources_title) },
            getDescription = { stringResource(Res.string.pref_database_source_database_sources_description) },
            getDefaultValue = { emptyList() }
        )
}
