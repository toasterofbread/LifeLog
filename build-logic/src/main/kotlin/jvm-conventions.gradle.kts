plugins {
    kotlin("multiplatform")
    id("dev.mokkery")
}

kotlin {
    jvm()

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonTest {
            addTestDependencies(project)
        }
    }
}
