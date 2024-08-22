import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import util.configureAllKmpTargets

plugins {
    id("kmp-conventions")
    id("android-library-conventions")

    alias(libs.plugins.kotlin)
    alias(libs.plugins.publish)
}

kotlin {
    configureAllKmpTargets { target ->
        if (target !is KotlinNativeTarget) {
            return@configureAllKmpTargets
        }

        target.compilations.getByName("main") {
            cinterops {
                val popen2 by creating
            }
        }
    }

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

        val wasmJsMain by getting {

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
val artifactName: String = "core.git"

android {
    namespace = "dev.toastbits.$projectName.$artifactName"
}
