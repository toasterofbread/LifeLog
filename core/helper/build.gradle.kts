import util.configureAllKmpTargets

plugins {
    id("kmp-conventions")
    id("android-library-conventions")
    id("publishing-conventions")

    alias(libs.plugins.kotlin)
    alias(libs.plugins.publish)
//    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    configureAllKmpTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.core.specification)
                api(projects.core.accessor)
            }
        }

        val jvmAndNativeMain by getting {
            dependencies {
                api(projects.core.git)
            }
        }
    }
}

val projectName: String = libs.versions.project.name.get()
val projectVersion: String = project.libs.versions.project.name.get()
val artifactName: String = "core.helper"

android {
    namespace = "dev.toastbits.$projectName.$artifactName"
}

mavenPublishing {
    coordinates("dev.toastbits.$projectName", artifactName, projectVersion)
}
