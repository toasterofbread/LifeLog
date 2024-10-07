import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import util.KmpTarget
import util.configureKmpTargets

plugins {
    id("kmp-conventions")
    id("android-library-conventions")

    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotules.definition)
}

kotlin {
    configureKmpTargets(KmpTarget.JVM, KmpTarget.JS) {
        if (this is KotlinJsTargetDsl) {
            binaries.executable()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.core.plugin)
                implementation(projects.core.specification)
            }
        }
    }
}
