import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import util.KmpTarget
import util.configureKmpTargets

plugins {
    id("kmp-conventions")
    id("android-library-conventions")

    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotules.definition)
}

kotlin {
    configureKmpTargets(KmpTarget.JVM, KmpTarget.WASMJS, KmpTarget.JS) {
        if (this is KotlinJsTargetDsl) {
            binaries.executable()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.core.plugin)
                implementation(libs.ktor.core)
            }
        }
    }
}

for (target in listOf("wasmJs", "js")) {
    tasks.named("${target}BrowserDistribution") {
        dependOnTaskAndCopyOutputDirectory(":extension:testext:jsBrowserDistribution", "productionExecutable")
    }

    tasks.named("${target}BrowserDevelopmentExecutableDistribution") {
        dependOnTaskAndCopyOutputDirectory(":extension:testext:jsBrowserDevelopmentExecutableDistribution", "developmentExecutable")
    }

    tasks.named("${target}BrowserDevelopmentRun") {
        dependOnTaskAndCopyOutputDirectory(
            ":extension:testext:jsBrowserDevelopmentExecutableDistribution",
            "developmentExecutable",
            project.layout.buildDirectory.asFile.get()
                .resolve("processedResources/$target/main").absolutePath,
            first = true
        )
    }
}


fun Task.dependOnTaskAndCopyOutputDirectory(taskPath: String, theirDir: String, ourDir: String = theirDir, first: Boolean = false) {
    dependsOn(taskPath)

    outputs.upToDateWhen { false }

    val configure: Task.() -> Unit = {
        val output: File? =
            if (ourDir.startsWith("/")) File(ourDir)
            else outputs.files.singleOrNull { it.name == ourDir }
        checkNotNull(output) { "$ourDir | ${outputs.files.map { it.absolutePath }}" }

        val taskParts: List<String> = taskPath.split(':').filter { it.isNotBlank() }
        var currentProject = rootProject
        for (i in 0 until taskParts.size - 1) {
            currentProject = currentProject.project(taskParts[i])
        }

        val workerBuildTask: Task by currentProject.tasks.named(taskParts.last())
        val workerProductionExecutable: File? = workerBuildTask.outputs.files.singleOrNull { it.name == theirDir }
        checkNotNull(workerProductionExecutable) { theirDir }

        for (file in workerProductionExecutable.listFiles().orEmpty()) {
            file.copyRecursively(output.resolve(file.name), overwrite = true)
        }
    }

    if (first) {
        doFirst(configure)
    }
    else {
        doLast(configure)
    }
}
