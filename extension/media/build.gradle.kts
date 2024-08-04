plugins {
    id("kmp-conventions")
    id("publishing-conventions")

    alias(libs.plugins.kotlin)
    alias(libs.plugins.publish)
//    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.specification)
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
