plugins {
    id("android-library-conventions")
    id("compose-conventions")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
            }
        }
    }
}
