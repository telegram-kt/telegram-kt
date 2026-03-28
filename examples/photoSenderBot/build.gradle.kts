plugins {
    kotlin("jvm")
}

group = "photoSenderBot"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(rootProject)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}