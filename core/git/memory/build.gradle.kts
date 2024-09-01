import util.configureAllKmpTargets

plugins {
    id("kmp-conventions")
    id("android-library-conventions")

    alias(libs.plugins.kotlin)
    alias(libs.plugins.publish)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    configureAllKmpTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.core.filestructure)
                api(projects.core.git.core)

                api(libs.okio)
                api(libs.kotlinx.datetime)
                api(libs.korlibs.compression)
                implementation(libs.ktor.core)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.hash.sha1)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.ktor.client.cio)
                implementation(libs.jzlib)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(projects.core.test)
            }
        }
    }
}
