import util.KmpTarget
import util.configureKmpTargets

plugins {
    id("kmp-conventions")
//    id("android-library-conventions")
    id("publishing-conventions")

    alias(libs.plugins.kotlin)
    alias(libs.plugins.publish)
    alias(libs.plugins.kotules.declaration)
}

kotlin {
    configureKmpTargets(KmpTarget.JVM, KmpTarget.WASMJS)

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.core.specification)
            }
        }
    }
}

val projectName: String = libs.versions.project.name.get()
val projectVersion: String = libs.versions.project.version.name.get()

mavenPublishing {
    coordinates("dev.toastbits.$projectName", "core.plugin", projectVersion)
}
