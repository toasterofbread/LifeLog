plugins {
    alias(libs.plugins.kotlin).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.publish).apply(false)
    alias(libs.plugins.sqldelight).apply(false)
    alias(libs.plugins.undercouch.download).apply(false)
}

// TEMP
// https://github.com/cashapp/sqldelight/pull/4965
subprojects {
    configurations.all {
        resolutionStrategy {
            eachDependency {
                if (requested.group.toString() == "app.cash.sqldelight") {
                    val sqldelightVersion: String = libs.versions.sqldelight.get()
                    useTarget("com.github.toasterofbread.sqldelight:${requested.name}:$sqldelightVersion")
                }
            }
        }
    }
}
