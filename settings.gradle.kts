@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")

    resolutionStrategy {
        eachPlugin {
            // TEMP
            // https://github.com/cashapp/sqldelight/pull/4965
            if (requested.id.toString() == "app.cash.sqldelight") {
                useModule("com.github.toasterofbread.sqldelight:app.cash.sqldelight.gradle.plugin:${requested.version}")
            }
        }
    }

    repositories {
        mavenLocal()

        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")

        // TEMP
        maven("https://jitpack.io")
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

        // https://github.com/d1snin/catppuccin-kotlin (in ComposeKit)
        maven("https://maven.d1s.dev/snapshots")

//        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "lifelog"

include(":application:app")
include(":application:worker")
include(":application:settings:domain")
include(":application:settings:data")
include(":application:dbsource:domain")
include(":application:dbsource:data")
include(":application:dbsource:inmemorygit")
include(":application:cache")

include(":core:filestructure")
include(":core:specification")
include(":core:accessor")
include(":core:git:core")
include(":core:git:system")
include(":core:git:memory")
include(":core:test")

include(":extension:media")
include(":extension:mediawatch")
include(":extension:gdocs")
