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
                implementation(projects.core.specification)
                implementation(projects.core.accessor)
                implementation(projects.core.git.memory)

                implementation(libs.composekit)
                implementation(libs.ktor.core)
                implementation(libs.kotlinx.serialization.json)
            }
        }
    }
}
