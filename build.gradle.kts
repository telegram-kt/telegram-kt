import org.gradle.plugins.signing.Sign
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.3.20"
    id("com.gradleup.nmcp") version "0.0.9"
    `maven-publish`
    signing
}

group = "io.github.telegram-kt"
version = "0.1.1"

repositories {
    mavenCentral()
}

dependencies {

    var ktorVersion = "3.4.1"

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    implementation("io.ktor:ktor-client-logging:${ktorVersion}")
    implementation("ch.qos.logback:logback-classic:1.5.13")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
    sourceSets.all {
        languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
    }
}

// === Sources and Javadoc JARs for publication ===
val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val javadocJar by tasks.creating(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.named("javadoc"))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(sourcesJar)
            artifact(javadocJar)

            groupId = project.group as String
            artifactId = "telegram-kt"

            pom {
                name.set("TelegramKt")
                description.set("Modern, type-safe, coroutine-first Telegram Bot API library for Kotlin")
                url.set("https://github.com/telegram-kt/telegram-kt")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("telegram-kt")
                        name.set("TelegramKt Contributors")
                        url.set("https://github.com/telegram-kt")
                    }
                }

                scm {
                    url.set("https://github.com/telegram-kt/telegram-kt")
                    connection.set("scm:git:git://github.com/telegram-kt/telegram-kt.git")
                    developerConnection.set("scm:git:ssh://github.com/telegram-kt/telegram-kt.git")
                }
            }
        }
    }
}

nmcp {
    publish("mavenJava") {
        username.set(System.getenv("SONATYPE_USERNAME"))
        password.set(System.getenv("SONATYPE_TOKEN"))
    }
}

// Configure signing only for release builds (via CI or env vars)
// For local development, signing is optional — publishToMavenLocal works without it
val useSigning = System.getenv("GPG_PRIVATE_KEY") != null && System.getenv("GPG_PASSPHRASE") != null

if (useSigning) {
    signing {
        useInMemoryPgpKeys(
            System.getenv("GPG_PRIVATE_KEY"),
            System.getenv("GPG_PASSPHRASE")
        )
        sign(publishing.publications["mavenJava"])
    }
} else {
    logger.lifecycle("Signing disabled - set GPG_PRIVATE_KEY and GPG_PASSPHRASE env vars for release builds")
    tasks.withType<Sign>().configureEach {
        enabled = false
    }
}