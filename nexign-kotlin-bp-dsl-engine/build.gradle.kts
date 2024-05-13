plugins {
    kotlin("jvm") version "1.9.0"
    id("io.ktor.plugin") version "2.3.11"
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
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")                         // for REST API
    implementation("io.ktor:ktor-server-swagger")
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-server-openapi")
    implementation("io.swagger.codegen.v3:swagger-codegen-generators:1.0.36")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")            // for JSON parsing
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    // Otherwise you'll get a "No main manifest attribute" error
    manifest {
        attributes["Main-Class"] = "com.nexign.dsl.engine.Main"
    }

    // To avoid the duplicate handling strategy error
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // To add all the dependencies otherwise a "NoClassDefFoundError" error
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("Main")
}