import util.configureAllComposeTargets

plugins {
    id("android-library-conventions")
    id("compose-conventions")
}

kotlin {
    configureAllComposeTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.application.worker)
                implementation(projects.core.specification)
                implementation(projects.core.accessor)
                implementation(projects.core.git.core)

                implementation(libs.composekit)
                implementation(libs.ktor.core)
            }
        }
    }
}
