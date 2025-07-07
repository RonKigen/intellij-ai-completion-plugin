import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML

plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.0"
    id("org.jetbrains.changelog") version "2.2.0"
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.serialization") version "2.2.0"
}

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    testImplementation("junit:junit:4.13.2")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

intellij {
    version.set("2024.2.5") // Make sure this matches your gradle.properties platformVersion
    type.set("IC")          // IntelliJ Community
    plugins.set(listOf("java"))

    downloadSources.set(false)
    instrumentCode.set(false)
}

changelog {
    version.set("${project.version}")
    path.set("${project.projectDir}/CHANGELOG.md")
}

tasks {
    patchPluginXml {
        changeNotes.set("""
            Initial release.
        """.trimIndent())
    }

    // Ensure the Kotlin compiler uses the right JVM target
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    // Reduce memory usage during tests
    withType<Test> {
        maxHeapSize = "1g"
    }
}
