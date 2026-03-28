plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "telegram-kt"
include("examples:echoBot")
include("examples:photoSenderBot")