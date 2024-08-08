import util.KmpTarget
import util.addTestDependencies
import util.configureKmpTargets

plugins {
    id("android-library-conventions")
    kotlin("multiplatform")
    id("dev.mokkery")
}

kotlin {
    configureKmpTargets(KmpTarget.values().filter { it != KmpTarget.WASMJS })

    sourceSets {
        commonTest {
            addTestDependencies(project)
        }
    }
}
