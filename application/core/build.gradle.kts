plugins {
    id("compose-application-conventions")

    alias(libs.plugins.kotlin)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.core.helper)

                implementation(projects.extension.media)
                implementation(projects.extension.mediawatch)
                implementation(projects.extension.gdocs)

                implementation(libs.composekit)
                implementation(libs.okio)

                // TEST
                implementation(libs.ktor.core)
                implementation(projects.core.git.memory)
            }
        }
    }
}
