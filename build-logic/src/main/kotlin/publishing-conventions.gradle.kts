import com.vanniktech.maven.publish.SonatypeHost
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.JavadocJar

plugins {
    id("com.vanniktech.maven.publish")
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    configure(KotlinMultiplatform(
        sourcesJar = true
    ))

    pom {
        name.set("lifelog")
        description.set("TODO")
        url.set("https://github.com/toasterofbread/LifeLog")
        inceptionYear.set("2024")

        licenses {
            license {
                name.set("GPL-3.0")
                url.set("https://www.gnu.org/licenses/gpl-3.0.html")
            }
        }
        developers {
            developer {
                id.set("toasterofbread")
                name.set("Talo Halton")
                email.set("talohalton@gmail.com")
                url.set("https://github.com/toasterofbread")
            }
        }
        scm {
            connection.set("https://github.com/toasterofbread/LifeLog.git")
            url.set("https://github.com/toasterofbread/LifeLog")
        }
        issueManagement {
            system.set("Github")
            url.set("https://github.com/toasterofbread/LifeLog/issues")
        }
    }
}
