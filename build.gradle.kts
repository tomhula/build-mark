plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.3.1"
}

group = "io.github.tomhula"
version = "1.0.2"

gradlePlugin {
    website = "https://github.com/tomhula/buildmark"
    vcsUrl = "https://github.com/tomhula/buildmark.git"
    plugins {
        create("buildMark") {
            id = "io.github.tomhula.buildmark"
            displayName = "BuildMark"
            description = "Gradle plugin for embedding build information (like project name, version,...) into the code so it can be read at runtime."
            tags = listOf("build", "version", "project", "kotlin")
            implementationClass = "io.github.tomhula.buildmark.BuildMark"
        }
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlinpoet)
    compileOnly(libs.kotlin.gradle.plugin)
    implementation(libs.gradle.idea.ext.plugin)

    testImplementation(kotlin("test"))
    testImplementation(libs.kotlin.scripting.common)
    testImplementation(libs.kotlin.scripting.jvm)
    testImplementation(libs.kotlin.scripting.jvm.host)
}

tasks.test {
    useJUnitPlatform()
}
