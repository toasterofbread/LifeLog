import util.configureAllKmpTargets

plugins {
    id("kmp-conventions")
    id("android-library-conventions")

    alias(libs.plugins.kotlin)
    alias(libs.plugins.publish)
}

kotlin {
    configureAllKmpTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.core.specification)
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        val notWasmJsMain by getting {
            dependencies {
                implementation(projects.core.git)
            }
        }
    }
}

val projectName: String = libs.versions.project.name.get()
val projectVersion: String = project.libs.versions.project.name.get()
val artifactName: String = "core.saver"

android {
    namespace = "dev.toastbits.$projectName.$artifactName"
}
