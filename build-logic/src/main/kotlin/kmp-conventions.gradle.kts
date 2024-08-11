import util.addTestDependencies

plugins {
    kotlin("multiplatform")
    id("dev.mokkery")
}

kotlin {
    sourceSets {
        all {
            languageSettings.enableLanguageFeature("ExpectActualClasses")
        }

        commonTest {
            addTestDependencies(project)
        }
    }
}
