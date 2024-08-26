@file:OptIn(
    org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class
)
package util

import org.gradle.kotlin.dsl.assign
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyBuilder
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

fun KotlinMultiplatformExtension.configureAllKmpTargets(onConfigure: (KotlinTarget) -> Unit = {}) {
    configureKmpTargets(*KmpTarget.values(), onConfigure = onConfigure)
}

fun KotlinMultiplatformExtension.configureKmpTargets(
    vararg targets: KmpTarget,
    onConfigure: (KotlinTarget) -> Unit = {}
) {
    for (target in targets) {
        when (target) {
            KmpTarget.JVM -> onConfigure(jvm())
            KmpTarget.ANDROID -> {
                androidTarget {
                    publishLibraryVariants("release")
                    compilerOptions {
                        jvmTarget = JvmTarget.JVM_1_8
                    }

                    onConfigure(this)
                }
            }
            KmpTarget.NATIVE -> {
                onConfigure(linuxX64())
                onConfigure(linuxArm64())
                onConfigure(mingwX64())
            }
            KmpTarget.WASMJS -> {
                wasmJs {
                    useCommonJs()

                    browser {
                        webpackTask {
                            output.libraryTarget = "commonjs2"
                        }

                        testTask {
                            useKarma {
                                useFirefox()
                            }
                        }
                    }

                    onConfigure(this)
                }
            }
        }
    }

    applyDefaultHierarchyTemplate {
        fun KotlinHierarchyBuilder.ifPresent(target: KmpTarget) {
            if (!targets.contains(target)) {
                return
            }

            when (target) {
                KmpTarget.JVM -> withJvm()
                KmpTarget.ANDROID -> withAndroidTarget()
                KmpTarget.NATIVE -> {
                    withLinuxX64()
                    withLinuxArm64()
                    withMingwX64()
                }
                KmpTarget.WASMJS -> withWasmJs()
            }
        }

        common {
            group("notWasmJs") {
                group("allJvm") {
                    ifPresent(KmpTarget.JVM)
                    ifPresent(KmpTarget.ANDROID)
                }
                ifPresent(KmpTarget.NATIVE)
            }

            group("jvmAndNative") {
                ifPresent(KmpTarget.JVM)

                group("native") {
                    ifPresent(KmpTarget.NATIVE)
                }
            }

            if (targets.contains(KmpTarget.ANDROID) && targets.contains(KmpTarget.WASMJS)) {
                group("androidAndWasmJs") {
                    ifPresent(KmpTarget.ANDROID)
                    ifPresent(KmpTarget.WASMJS)
                }
            }

            ifPresent(KmpTarget.WASMJS)
        }
    }
}
