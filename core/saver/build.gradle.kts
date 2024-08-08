plugins {
    id("kmp-conventions")

    alias(libs.plugins.kotlin)
    alias(libs.plugins.publish)
}

kotlin {
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
