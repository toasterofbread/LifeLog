import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

internal fun KotlinSourceSet.addTestDependencies(project: Project) {
    dependencies {
        implementation(kotlin("test"))
        implementation(project.libs.library("assertk"))
        implementation(project.libs.library("kotlinx.coroutines.test"))
    }
}
