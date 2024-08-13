import util.addTestDependencies

plugins {
    kotlin("multiplatform")
    id("dev.mokkery")
}

kotlin {
    sourceSets {
        all {
            languageSettings.apply {
                enableLanguageFeature("ExpectActualClasses")
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }

        commonTest {
            addTestDependencies(project)
        }
    }
}
