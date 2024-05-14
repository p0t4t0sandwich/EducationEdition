import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

plugins {
    kotlin("jvm") version("1.9.23")
    id("com.diffplug.spotless") version("6.25.0")
    id("com.github.johnrengelman.shadow") version("8.1.1")
}

group = "dev.neuralnexus.scifi"
version = "1.1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

spotless {
    format("misc") {
        target("*.gradle", ".gitattributes", ".gitignore")

        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }
    kotlin {
        ktlint("1.2.1")
        target("src/**/*.kt")
        ktfmt()
        licenseHeader ("""/**
 * Copyright (c) 2024 Dylan Sperrer - dylan@sperrer.ca
 * The project is Licensed under GPL-3.0
 */

""")
    }
}

tasks.named<ShadowJar>("shadowJar") {
    dependencies {
        exclude("META-INF/*", "META-INF/**/*")
    }
    relocate("com.google.gson", "${group}.lib.gson")

    minimize()

    archiveFileName = "MCWorldAnalyzer-${version}.jar"

    manifest {
        attributes(mapOf(
            "Specification-Title" to "MCWorldAnalyzer",
            "Specification-Version" to version,
            "Specification-Vendor" to "NeuralNexus",
            "Implementation-Version" to version,
            "Implementation-Vendor" to "NeuralNexus",
            "Implementation-Timestamp" to DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").format(ZonedDateTime.now()),
            "Main-Class" to "${project.group}.MainKt"
        ))
    }
}

tasks.build {
     dependsOn("spotlessApply")
}

artifacts {
    archives(tasks.shadowJar)
}
