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
                implementation(libs.markdown)
                api(libs.kotlinx.datetime)
//                implementation(libs.kotlinx.serialization.json)
            }
        }
    }
}

val projectName: String = libs.versions.project.name.get()
val projectVersion: String = project.libs.versions.project.name.get()
val artifactName: String = "core.specification"

android {
    namespace = "dev.toastbits.$projectName.$artifactName"
}

mavenPublishing {
    coordinates("dev.toastbits.$projectName", artifactName, projectVersion)
}
