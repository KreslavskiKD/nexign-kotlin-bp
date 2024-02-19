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
    implementation(project(":nexign-kotlin-bp-dsl-scenarios"))
    testImplementation(kotlin("test"))
    implementation(kotlin("reflect"))

    implementation("com.github.ajalt.clikt:clikt:4.2.2")
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