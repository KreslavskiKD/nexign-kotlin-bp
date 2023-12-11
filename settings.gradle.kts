rootProject.name = "nexign-kotlin-bp"

include("nexign-kotlin-bp-dsl")
include("nexign-kotlin-bp-dsl-drive")
include("nexign-kotlin-bp-dsl-scenarios")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
