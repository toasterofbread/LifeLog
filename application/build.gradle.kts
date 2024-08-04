plugins {
    id("compose-application-conventions")

    alias(libs.plugins.kotlin)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":specification"))
                implementation(project(":api"))

                implementation(libs.ktor.core)
            }
        }
    }
}

android {
    namespace = "dev.toastbits." + libs.versions.project.name.get()
}
