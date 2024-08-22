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

                // TEST
                implementation(libs.ktor.core)
                implementation(projects.core.git)
            }
        }

        val desktopMain by getting {
            dependencies {
                // TEST
                implementation(libs.ktor.client.cio)
            }
        }

        val wasmJsMain by getting {
            dependencies {
                implementation(npm("@es-git/memory-repo", "0.10.x"))
            }
        }
    }
}

android {
    namespace = "dev.toastbits." + libs.versions.project.name.get()
}
