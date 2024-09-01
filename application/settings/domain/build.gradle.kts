plugins {
    id("android-library-conventions")
    id("compose-conventions")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.application.dbsource.domain)
                api(projects.core.specification)
                api(projects.core.accessor)
                api(projects.core.git.core)

                implementation(libs.composekit)
                implementation(libs.kotlinx.serialization.json)
            }
        }
    }
}
