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
include(":specification")
include(":api")
include(":application")
include(":extension:media")
