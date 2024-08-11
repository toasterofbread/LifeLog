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
                implementation(projects.core.specification)
                implementation(projects.core.accessor)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(projects.core.accessor)
                implementation(projects.core.test)
            }
        }
    }
}

val projectName: String = libs.versions.project.name.get()
val projectVersion: String = project.libs.versions.project.name.get()
val artifactName: String = "gdocs"

android {
    namespace = "dev.toastbits.$projectName.extension.$artifactName"
}

mavenPublishing {
    coordinates("dev.toastbits.$projectName.extension", artifactName, projectVersion)
}
