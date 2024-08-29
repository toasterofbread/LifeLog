import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import util.KmpTarget
import util.configureAllKmpTargets
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
    ) {
        if (this !is KotlinNativeTarget) {
            return@configureKmpTargets
        }

        compilations.getByName("main") {
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

        val commonTest by getting {
            dependencies {
                implementation(projects.core.test)
            }
        }
    }
}
