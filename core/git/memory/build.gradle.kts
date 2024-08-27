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
                api(projects.core.filestructure)

                api(libs.okio)
                api(libs.kotlinx.datetime)
                implementation(libs.ktor.core)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.hash.sha1)
                api(libs.korlibs.compression)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.ktor.client.cio)
                implementation(libs.jzlib)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(projects.core.test)
            }
        }
    }
}
