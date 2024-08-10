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
            }
        }
    }
}

val projectName: String = libs.versions.project.name.get()
val projectVersion: String = project.libs.versions.project.name.get()
val artifactName: String = "media"

android {
    namespace = "dev.toastbits.$projectName.extension.$artifactName"
}

mavenPublishing {
    coordinates("dev.toastbits.$projectName.extension", artifactName, projectVersion)
}
