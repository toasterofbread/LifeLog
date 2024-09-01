plugins {
    id("android-application-conventions")
    id("compose-conventions")

    alias(libs.plugins.kotlin)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.application.dbsource.data)
                implementation(projects.application.settings.data)
                implementation(projects.application.worker)

                implementation(projects.extension.media)
                implementation(projects.extension.mediawatch)
                implementation(projects.extension.gdocs)

                implementation(libs.composekit)
                implementation(libs.okio)

                // TEST
                implementation(libs.ktor.core)
                implementation(projects.core.git.memory)
            }
        }
    }
}

tasks.named("wasmJsBrowserDistribution") {
    dependOnTaskAndCopyOutputDirectory(":application:worker:wasmJsBrowserDistribution", "productionExecutable")
}

tasks.named("wasmJsBrowserDevelopmentExecutableDistribution") {
    dependOnTaskAndCopyOutputDirectory(":application:worker:wasmJsBrowserDevelopmentExecutableDistribution", "developmentExecutable")
}

fun Task.dependOnTaskAndCopyOutputDirectory(taskPath: String, dirName: String) {
    dependsOn(taskPath)

    doLast {
        val appProductionExecutable: File = outputs.files.single { it.name == dirName }

        val taskParts: List<String> = taskPath.split(':').filter { it.isNotBlank() }
        var currentProject = rootProject
        for (i in 0 until taskParts.size - 1) {
            currentProject = currentProject.project(taskParts[i])
        }

        val workerBuildTask: Task by currentProject.tasks.named(taskParts.last())
        val workerProductionExecutable: File = workerBuildTask.outputs.files.single { it.name == dirName }

        for (file in workerProductionExecutable.listFiles().orEmpty()) {
            file.copyRecursively(appProductionExecutable.resolve(file.name), overwrite = true)
        }
    }
}
