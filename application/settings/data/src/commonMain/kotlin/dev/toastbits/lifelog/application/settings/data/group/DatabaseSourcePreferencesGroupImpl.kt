package dev.toastbits.lifelog.application.settings.data.group

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.platform.PreferencesGroupImpl
import dev.toastbits.composekit.platform.PreferencesProperty
import dev.toastbits.composekit.settings.ui.component.item.SettingsItem
import dev.toastbits.composekit.settings.ui.component.item.TextFieldSettingsItem
import dev.toastbits.lifelog.application.dbsource.domain.type.DatabaseSourceTypeRegistry
import dev.toastbits.lifelog.application.dbsource.inmemorygit.type.InMemoryGitDatabaseSourceType
import dev.toastbits.lifelog.application.settings.domain.group.DatabaseSourcePreferencesGroup
import dev.toastbits.lifelog.application.settings.domain.model.SerialisedDatabaseSourceConfiguration
import lifelog.application.settings.data.generated.resources.Res
import lifelog.application.settings.data.generated.resources.prefs_group_database_source_description
import lifelog.application.settings.data.generated.resources.prefs_group_database_source_title
import lifelog.application.settings.data.generated.resources.pref_database_source_git_username_title
import lifelog.application.settings.data.generated.resources.pref_database_source_git_username_description
import lifelog.application.settings.data.generated.resources.pref_database_source_git_password_title
import lifelog.application.settings.data.generated.resources.pref_database_source_git_password_description
import org.jetbrains.compose.resources.stringResource

open class DatabaseSourcePreferencesGroupImpl(
    preferences: PlatformPreferences
): PreferencesGroupImpl("DATABASE_SOURCE", preferences), DatabaseSourcePreferencesGroup {
    @Composable
    override fun getTitle(): String = stringResource(Res.string.prefs_group_database_source_title)

    @Composable
    override fun getDescription(): String = stringResource(Res.string.prefs_group_database_source_description)

    @Composable
    override fun getIcon(): ImageVector = Icons.Default.Cloud

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
            getName = { throw IllegalStateException("Internal property") },
            getDescription = { throw IllegalStateException("Internal property") },
            getDefaultValue = { emptyList() }
        )

    override val GIT_USERNAME: PreferencesProperty<String> by
        property(
            getName = { stringResource(Res.string.pref_database_source_git_username_title) },
            getDescription = { stringResource(Res.string.pref_database_source_git_username_description) },
            getDefaultValue = { "" }
        )

    override val GIT_PASSWORD: PreferencesProperty<String> by
        property(
            getName = { stringResource(Res.string.pref_database_source_git_password_title) },
            getDescription = { stringResource(Res.string.pref_database_source_git_password_description) },
            getDefaultValue = { "" }
        )

    // AUTO_OPEN_SOURCE_INDEX ana DATABASE_SOURCES edited in DatabaseSourceListScreen
    override fun getConfigurationItems(): List<SettingsItem> =
        listOf(
            TextFieldSettingsItem(GIT_USERNAME),
            TextFieldSettingsItem(GIT_PASSWORD)
        )
}
