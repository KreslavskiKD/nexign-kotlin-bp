plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "com.nexign_internship"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":nexign-internship-kotlin-bp-dsl"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}
