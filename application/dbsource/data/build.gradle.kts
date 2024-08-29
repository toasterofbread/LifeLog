plugins {
    id("android-library-conventions")
    id("compose-conventions")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.application.dbsource.domain)
                implementation(projects.application.settings.domain)
                implementation(projects.application.settings.data)

                implementation(projects.core.specification)
                implementation(libs.composekit)
                implementation(libs.ktor.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.voyager.navigator)
            }
        }
    }
}
