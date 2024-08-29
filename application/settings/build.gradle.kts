plugins {
    id("android-library-conventions")
    id("compose-conventions")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.application.core)
                implementation(projects.application.dbsource)

                implementation(libs.composekit)
                implementation(libs.kotlinx.serialization.json)
            }
        }
    }
}
