plugins {
    id("android-library-conventions")
    id("compose-conventions")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.core.specification)
                implementation(projects.core.accessor)

                implementation(libs.composekit)
                implementation(libs.ktor.core)
            }
        }
    }
}
