plugins {
    alias(libs.plugins.kotlin).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.publish).apply(false)
    alias(libs.plugins.sqldelight).apply(false)
    alias(libs.plugins.undercouch.download).apply(false)
}

// TEMP SqlDelight
subprojects {
    configurations.all {
        resolutionStrategy {
            eachDependency {
                if (requested.group == "app.cash.sqldelight" && requested.name != "android-driver") {
                    val sqldelightVersion: String = libs.versions.sqldelight.wasm.get()
                    useTarget("com.github.toasterofbread.sqldelight:${requested.name}:$sqldelightVersion")
                }
            }
        }
    }
}
