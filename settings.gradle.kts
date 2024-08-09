@file:Suppress("UnstableApiUsage")

include(":application:androidMain")


pluginManagement {
    includeBuild("build-logic")

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

rootProject.name = "lifelog"
include(":core:specification")
include(":core:saver")
include(":core:git")
include(":core:test")
include(":application")
include(":extension:media")
