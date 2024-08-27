package util

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.Project

fun BaseExtension.configureAndroid(project: Project) {
    defaultConfig {
        applicationId = "dev.toastbits." + project.libs.version("project.name")
        versionCode = project.libs.version("project.version.inc").toInt()
        versionName = project.libs.version("project.version")

        targetSdk = project.libs.version("android.sdk.target").toInt()
        minSdk = project.libs.version("android.sdk.min").toInt()
    }

    if (this is CommonExtension<*, *, *, *, *, *>) {
        compileSdk = project.libs.version("android.sdk.compile").toInt()
    }

//    val projectVersion: String = project.libs.version("project.version")

    val namespaceParts: MutableList<String> = mutableListOf()
    var currentProject: Project? = project
    while (currentProject != null) {
        namespaceParts.add(currentProject.name)
        println(project.name)
        currentProject = currentProject.parent
    }

    namespace = "dev.toastbits." + namespaceParts.asReversed().joinToString(".")
}
