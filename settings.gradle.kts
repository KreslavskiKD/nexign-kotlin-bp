rootProject.name = "nexign-internship-kotlin-bp"

include("nexign-internship-kotlin-bp-dsl")
include("nexign-internship-kotlin-bp-dsl-engine")
include("nexign-internship-kotlin-bp-dsl-scenarios")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
