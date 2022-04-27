import java.net.URI
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    signing
}

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("kotlinx-gettext") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])

            pom {
                name.set("Kotlinx Gettext")
                description.set("Kotlin Gettext i18n library")
                url.set("https://github.com/kropp/kotlinx-gettext")

                scm {
                    connection.set("scm:git:git://github.com/kropp/kotlinx-gettext")
                    developerConnection.set("scm:git:git://github.com/kropp/kotlinx-gettext")
                    url.set("https://github.com/kropp/kotlinx-gettext/tree/main")
                }

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("kropp")
                        name.set("Victor Kropp")
                        email.set("victor@kropp.name")
                    }
                }
            }
        }
    }

    repositories {
        maven {
            url = URI("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials(PasswordCredentials::class.java)
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["kotlinx-gettext"])
}
