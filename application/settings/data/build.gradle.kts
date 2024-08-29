plugins {
    id("android-library-conventions")
    id("compose-conventions")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.application.settings.domain)
                implementation(projects.application.dbsource.domain)
                implementation(projects.application.dbsource.inmemorygit)

                implementation(libs.composekit)
            }
        }
    }
}
