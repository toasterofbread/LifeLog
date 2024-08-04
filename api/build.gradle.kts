plugins {
    id("kmp-conventions")
    id("publishing-conventions")

    alias(libs.plugins.kotlin)
    alias(libs.plugins.publish)
}

val projectName: String = libs.versions.project.name.get()
val projectVersion: String = project.libs.versions.project.name.get()
val artifactName: String = "api"

android {
    namespace = "dev.toastbits.$projectName.$artifactName"
}

mavenPublishing {
    coordinates("dev.toastbits.$projectName", artifactName, projectVersion)
}
