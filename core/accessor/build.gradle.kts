import util.configureAllKmpTargets

plugins {
    id("kmp-conventions")
    id("android-library-conventions")

    alias(libs.plugins.kotlin)
    alias(libs.plugins.publish)
}

kotlin {
    configureAllKmpTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.core.specification)
                api(projects.core.filestructure)

                api(libs.kotlinx.coroutines.core)
                api(libs.okio)
            }
        }

        val jvmAndNativeMain by getting {
            dependencies {
                implementation(projects.core.git.system)
            }
        }

        val jvmAndNativeTest by getting {
            dependencies {
                implementation(projects.core.test)
                // temp
                implementation(projects.extension.mediawatch)
            }
        }
    }
}
