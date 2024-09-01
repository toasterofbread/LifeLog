import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinWasmJsTargetDsl
import util.KmpTarget
import util.configureKmpTargets
import util.library
import util.libs

plugins {
    id("android-library-conventions")
    id("kmp-conventions")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    configureKmpTargets(
        *KmpTarget.ALL_COMPOSE,
        beforeConfigure = {
            when (this) {
                is KotlinWasmJsTargetDsl -> {
                    browser {
                        commonWebpackConfig {
                            outputFileName = "worker.js"
                        }
                    }
                    binaries.executable()
                }
            }
        }
    )

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.library("kotlinx.coroutines.core"))
                implementation(libs.library("kotlinx.serialization.json"))
            }
        }
    }
}
