@file:OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)
package util

import org.gradle.kotlin.dsl.assign
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyBuilder
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

fun KotlinMultiplatformExtension.configureAllKmpTargets() {
    configureKmpTargets(*KmpTarget.values())
}

fun KotlinMultiplatformExtension.configureKmpTargets(vararg targets: KmpTarget) {
    for (target in targets) {
        when (target) {
            KmpTarget.JVM -> jvm()
            KmpTarget.ANDROID -> {
                androidTarget {
                    publishLibraryVariants("release")
                    compilerOptions {
                        jvmTarget = JvmTarget.JVM_1_8
                    }
                }
            }
            KmpTarget.NATIVE -> {
                linuxX64()
                linuxArm64()
                mingwX64()

            }
            KmpTarget.WASMJS -> {
                wasmJs {
                    browser()
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

            ifPresent(KmpTarget.WASMJS)
        }
    }
}
