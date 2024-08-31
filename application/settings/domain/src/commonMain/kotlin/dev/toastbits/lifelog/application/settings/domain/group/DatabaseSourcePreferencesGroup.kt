package dev.toastbits.lifelog.application.settings.domain.group

import dev.toastbits.composekit.platform.PreferencesGroup
import dev.toastbits.composekit.platform.PreferencesProperty
import dev.toastbits.lifelog.application.dbsource.domain.type.DatabaseSourceTypeRegistry
import dev.toastbits.lifelog.application.settings.domain.model.SerialisedDatabaseSourceConfiguration

@Suppress("PropertyName")
interface DatabaseSourcePreferencesGroup: PreferencesGroup {
    val sourceTypeRegistry: DatabaseSourceTypeRegistry

    val AUTO_OPEN_SOURCE_INDEX: PreferencesProperty<Int>
    val DATABASE_SOURCES: PreferencesProperty<List<SerialisedDatabaseSourceConfiguration>>

    val GIT_USERNAME: PreferencesProperty<String>
    val GIT_PASSWORD: PreferencesProperty<String>
}
