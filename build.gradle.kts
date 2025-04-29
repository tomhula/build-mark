plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.3.1"
}

group = "cz.tomashula"
version = "1.0-SNAPSHOT"


gradlePlugin {
    website = "https://github.com/tomhula/build-mark"
    vcsUrl = "https://github.com/tomhula/build-mark.git"
    plugins {
        create("buildMark") {
            id = "cz.tomashula.buildmark"
            displayName = "BuildMark"
            description = "Gradle plugin for embedding build information (like project name, version,...) into the code so it can be read at runtime."
            tags = listOf("build", "version", "project", "kotlin")
            implementationClass = "cz.tomashula.buildmark.BuildMark"
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlinpoet)
    compileOnly(libs.kotlin.gradle.plugin)
    
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlin.scripting.common)
    testImplementation(libs.kotlin.scripting.jvm)
    testImplementation(libs.kotlin.scripting.jvm.host)
}

tasks.test {
    useJUnitPlatform()
}