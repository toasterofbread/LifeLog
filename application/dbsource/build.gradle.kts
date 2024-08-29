plugins {
    id("android-library-conventions")
    id("compose-conventions")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.core.specification)
                implementation(libs.composekit)
                implementation(libs.kotlinx.serialization.json)
            }
        }
    }
}
