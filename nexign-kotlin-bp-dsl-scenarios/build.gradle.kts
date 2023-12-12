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
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}
