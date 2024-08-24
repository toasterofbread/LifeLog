import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
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
                api(libs.okio)
                implementation(libs.ktor.core)
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.ktor.client.cio)
                implementation("com.jcraft:jzlib:1.1.3")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(projects.core.test)
            }
        }
    }
}

val projectName: String = libs.versions.project.name.get()
val projectVersion: String = project.libs.versions.project.name.get()
val artifactName: String = "core.git.memoty"

android {
    namespace = "dev.toastbits.$projectName.$artifactName"
}
