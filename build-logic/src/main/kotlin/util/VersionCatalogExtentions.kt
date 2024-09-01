package util

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun VersionCatalog.version(path: String): String =
    findVersion(path).get().requiredVersion

fun VersionCatalog.library(path: String): MinimalExternalModuleDependency =
    findLibrary(path).get().get()

fun VersionCatalog.plugin(path: String): String =
    findPlugin(path).get().get().pluginId
