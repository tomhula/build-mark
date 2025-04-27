plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

group = "cz.tomashula"
version = "1.0-SNAPSHOT"


gradlePlugin {
    plugins {
        create("buildMark") {
            id = "cz.tomashula.buildmark"
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
    testImplementation("org.jetbrains.kotlin:kotlin-scripting-common")
    testImplementation("org.jetbrains.kotlin:kotlin-scripting-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host")
}

tasks.test {
    useJUnitPlatform()
}