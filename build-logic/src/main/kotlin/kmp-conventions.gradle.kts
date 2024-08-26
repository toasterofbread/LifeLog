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
                enableLanguageFeature("WasmUseNewExceptionProposal")
                optIn("kotlin.ExperimentalStdlibApi")
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }

        commonTest {
            addTestDependencies(project)
        }
    }
}
