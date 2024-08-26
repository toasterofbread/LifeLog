@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()

        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
        maven("https://jitpack.io")
    }
}

rootProject.name = "lifelog"
include(":application:commonMain")
include(":core:filestructure")
include(":core:specification")
include(":core:accessor")
include(":core:git:system")
include(":core:git:memory")
include(":core:helper")
include(":core:test")
include(":application")
include(":extension:media")
include(":extension:mediawatch")
include(":extension:gdocs")
