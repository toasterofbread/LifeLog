import util.addTestDependencies

plugins {
    kotlin("multiplatform")
    id("dev.mokkery")
}

kotlin {
    sourceSets {
        commonTest {
            addTestDependencies(project)
        }
    }
}
