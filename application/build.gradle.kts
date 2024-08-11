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

                implementation(libs.okio)
//                implementation(libs.ktor.core)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(projects.core.git)
            }
        }
    }
}

android {
    namespace = "dev.toastbits." + libs.versions.project.name.get()
}
