import util.KmpTarget
import util.addTestDependencies
import util.configureKmpTargets

plugins {
    id("android-library-conventions")

    kotlin("multiplatform")
    id("dev.mokkery")
}

kotlin {
    configureKmpTargets(KmpTarget.values().toList())

    sourceSets {
        commonTest {
            addTestDependencies(project)
        }
    }
}
