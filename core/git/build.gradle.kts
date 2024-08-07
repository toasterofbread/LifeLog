plugins {
    id("jvm-conventions")

    alias(libs.plugins.kotlin)
    alias(libs.plugins.publish)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.kgit)
            }
        }
    }
}
