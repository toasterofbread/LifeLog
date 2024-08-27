import util.configureAllKmpTargets

plugins {
    id("kmp-conventions")
    id("android-library-conventions")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    configureAllKmpTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.core.specification)
                implementation(libs.kotlinx.serialization.json)
            }
        }
    }
}
