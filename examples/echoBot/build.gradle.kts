plugins {
    kotlin("jvm")
}

group = "echoBot"
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