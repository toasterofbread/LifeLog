@file:OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)

import gradle.kotlin.dsl.accessors._721f887fc08d5057796897da5d49e236.sourceSets
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    id("android-library-conventions")

    kotlin("multiplatform")
    id("dev.mokkery")
}

kotlin {
    jvm()

    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
    }

    linuxX64()
    linuxArm64()
    mingwX64()

    wasmJs {
        browser()
    }

    applyDefaultHierarchyTemplate {
        common {
            group("allJvm") {
                withAndroidTarget()
                withJvm()
            }
            withLinuxX64()
            withLinuxArm64()
            withMingwX64()
            withWasmJs()
        }
    }

    sourceSets {
        commonTest {
            addTestDependencies(project)
        }
    }
}
