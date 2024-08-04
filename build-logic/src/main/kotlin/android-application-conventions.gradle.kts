plugins {
    id("com.android.application")
}

android {
    defaultConfig {
        applicationId = "dev.toastbits." + libs.version("project.name")
        versionCode = libs.version("project.version.inc").toInt()
        versionName = libs.version("project.version")

        targetSdk = libs.version("android.sdk.target").toInt()
        minSdk = libs.version("android.sdk.min").toInt()
    }
    compileSdk = libs.version("android.sdk.compile").toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
