plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "com.nexign"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":nexign-kotlin-bp-dsl"))

    testImplementation(kotlin("test"))

    implementation(kotlin("reflect"))
    implementation("com.github.ajalt.clikt:clikt:4.2.2")                // for CLI
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")            // for json parsing
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}