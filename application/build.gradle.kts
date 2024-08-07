plugins {
    id("compose-application-conventions")

    alias(libs.plugins.kotlin)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.core.specification)

                implementation(libs.ktor.core)
            }
        }
    }
}

android {
    namespace = "dev.toastbits." + libs.versions.project.name.get()
}
