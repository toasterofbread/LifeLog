import util.KmpTarget
import util.configureKmpTargets

plugins {
    id("kmp-conventions")

    alias(libs.plugins.kotlin)
    alias(libs.plugins.publish)
}

kotlin {
    configureKmpTargets(
        KmpTarget.JVM,
        KmpTarget.NATIVE
    )

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
    }
}
