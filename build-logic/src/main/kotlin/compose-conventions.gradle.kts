import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinWasmJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import util.KmpTarget
import util.configureKmpTargets
import util.library
import util.libs
import util.version

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("kmp-conventions")
    id("org.jetbrains.compose")
}

val projectName: String = libs.version("project.name")

kotlin {
    configureKmpTargets(
        *KmpTarget.ALL_COMPOSE,
        beforeConfigure = {
            when (this) {
                is KotlinWasmJsTargetDsl -> {
                    moduleName = projectName
                    browser {
                        commonWebpackConfig {
                            outputFileName = "composeApp.js"
                            devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                                static = (static ?: mutableListOf()).apply {
                                    // Serve sources to debug inside browser
                                    add(project.projectDir.path)
                                    add(project.projectDir.path + "/commonMain/")
                                    add(project.projectDir.path + "/wasmJsMain/")
                                }
                            }
                        }
                    }
                    binaries.executable()
                }
            }
        }
    )
    sourceSets {
        all {
            languageSettings.apply {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
                optIn("androidx.compose.foundation.ExperimentalFoundationApi")
                optIn("androidx.compose.foundation.layout.ExperimentalLayoutApi")
                optIn("androidx.compose.material3.ExperimentalMaterial3Api")
                optIn("androidx.compose.material.ExperimentalMaterialApi")
                optIn("androidx.compose.ui.ExperimentalComposeUiApi")
            }
        }

        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(compose.materialIconsExtended)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.library("androidx.activity.compose"))
            }
        }

        val wasmJsMain by getting {
            dependencies {
            }
        }
    }
}

compose {
    desktop {
        application {
            mainClass = "dev.toastbits.$projectName.application.core.MainKt"
        }
    }

    resources {
        publicResClass = true
    }
}
