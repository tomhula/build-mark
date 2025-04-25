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
    implementation("com.squareup:kotlinpoet:2.1.0")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.20")
}