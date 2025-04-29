# BuildMark Gradle Plugin

A Gradle plugin that generates a Kotlin object with build information at compile time. This allows you to access build-time properties in your application at runtime.

## Features

- Generates a Kotlin object with build information
- Customizable package name and object name
- Supports various property types (String, Int, Boolean, etc.)
- Automatically includes project version by default
- Seamlessly integrates with Kotlin compilation

## Requirements

- Gradle 7.0+
- Kotlin Gradle Plugin

## Installation

Add the plugin to your build script:

```kotlin
plugins {
    id("cz.tomashula.buildmark") version "1.0-SNAPSHOT"
}
```

## Usage

Once applied, the plugin will generate a `BuildMark` object (by default) that contains build information. You can access this object in your code:

```kotlin
// Access the generated BuildMark object
val version = BuildMark.VERSION
println("Application version: $version")
```

The generated code is automatically added to your Kotlin source sets, so it's available during compilation.

## Configuration

You can configure the plugin using the `buildMark` extension:

```kotlin
buildMark {
    // Package for the generated object (default: root package)
    targetPackage.set("com.example.app")

    // Name of the generated object (default: "BuildMark")
    targetObjectName.set("AppBuildInfo")

    // Custom properties to include in the generated object
    options.set(mapOf(
        "VERSION" to project.version,
        "BUILD_TIME" to System.currentTimeMillis(),
        "GIT_COMMIT" to "abc123", // You could use a Git plugin to get the actual commit
        "DEBUG" to true
    ))

    // Specify which Kotlin source sets should include the generated code
    // By default, it's added to the first source set
    kotlinSourceSets.set(listOf(kotlin.sourceSets.getByName("main")))
}
```

## Example

Here's a complete example of how to use the plugin:

1. Apply the plugin in your `build.gradle.kts`:

```kotlin
plugins {
    kotlin("jvm") version "1.8.0"
    id("cz.tomashula.buildmark") version "1.0-SNAPSHOT"
}

buildMark {
    targetPackage.set("com.example.app")
    options.set(mapOf(
        "VERSION" to project.version,
        "BUILD_TIME" to System.currentTimeMillis(),
        "DEBUG" to project.hasProperty("debug")
    ))
}
```

2. Use the generated object in your code:

```kotlin
fun main() {
    println("Running application version ${BuildMark.VERSION}")
    println("Built at: ${BuildMark.BUILD_TIME}")

    if (BuildMark.DEBUG) {
        println("Debug mode is enabled")
    }
}
```

## How It Works

The plugin:
1. Creates a task called `generateBuildMark`
2. Generates a Kotlin object with the specified properties
3. Adds the generated code to your Kotlin source sets
4. Ensures the generation happens before Kotlin compilation

The generated code is placed in the `build/generated/buildmark/` directory.

## License

[MIT License](LICENSE)

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
