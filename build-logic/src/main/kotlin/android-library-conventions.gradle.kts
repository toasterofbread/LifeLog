plugins {
    id("com.android.library")
}

android {
    compileSdk = libs.version("android.sdk.compile").toInt()
    defaultConfig {
        minSdk = libs.version("android.sdk.min").toInt()
    }
}
