@file:OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)
package util

import org.gradle.kotlin.dsl.assign
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyBuilder
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

fun KotlinMultiplatformExtension.configureAllKmpTargets(beforeConfigure: KotlinTarget.() -> Unit = {}, afterConfigure: KotlinTarget.() -> Unit = {}) {
    configureKmpTargets(*KmpTarget.values(), beforeConfigure = beforeConfigure, afterConfigure = afterConfigure)
}

fun KotlinMultiplatformExtension.configureKmpTargets(
    vararg targets: KmpTarget,
    beforeConfigure: KotlinTarget.() -> Unit = {},
    afterConfigure: KotlinTarget.() -> Unit = {}
) {
    for (target in targets) {
        when (target) {
            KmpTarget.JVM -> {
                jvm {
                    beforeConfigure()
                    afterConfigure()
                }
            }
            KmpTarget.ANDROID -> {
                androidTarget {
                    beforeConfigure()

//                    publishLibraryVariants("release")
                    compilerOptions {
                        jvmTarget = JvmTarget.JVM_17
                    }

                    afterConfigure()
                }
            }
            KmpTarget.NATIVE -> {
                val nativeTargets: List<KotlinNativeTarget> =
                    listOf(
                        linuxX64(),
                        linuxArm64(),
                        mingwX64()
                    )
                for (nativeTarget in nativeTargets) {
                    beforeConfigure(nativeTarget)
                    afterConfigure(nativeTarget)
                }
            }
            KmpTarget.WASMJS -> {
                wasmJs {
                    beforeConfigure()

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

                    afterConfigure()
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
