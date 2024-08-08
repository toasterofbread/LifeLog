plugins {
    id("kmp-nowasm-conventions")

    alias(libs.plugins.kotlin)
    alias(libs.plugins.publish)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.okio)
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.kgit)
            }
        }
    }
}

val projectName: String = libs.versions.project.name.get()
val projectVersion: String = project.libs.versions.project.name.get()
val artifactName: String = "core.git"

android {
    namespace = "dev.toastbits.$projectName.$artifactName"
}
