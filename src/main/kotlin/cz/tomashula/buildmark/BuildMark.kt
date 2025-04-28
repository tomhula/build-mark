package cz.tomashula.buildmark

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class BuildMark : Plugin<Project>
{
    override fun apply(project: Project)
    {
        val outputDirectory = project.layout.buildDirectory.dir(OUTPUT_DIR)

        val extension = project.extensions.create<BuildMarkExtension>("buildMark")

        configureExtensionConventions(extension, project)
        addBuildMarkToSourceSets(extension.kotlinSourceSets.get(), outputDirectory)

        val generateTask = project.tasks.register<GenerateBuildMarkTask>("generateBuildMark") {
            group = "build"
            description = "Generates the build mark object"
            outputDir.set(outputDirectory)
        }

        project.tasks.withType<KotlinCompile> {
            dependsOn(generateTask)
        }

        project.afterEvaluate {
            generateTask.get().generate()
        }
    }

    private fun configureExtensionConventions(extension: BuildMarkExtension, project: Project)
    {
        extension.targetPackage.convention("")
        extension.targetObjectName.convention("BuildMark")
        project.kotlinExtension.sourceSets.firstOrNull()?.let { firstKotlinSourceSet ->
            extension.kotlinSourceSets.convention(listOf(firstKotlinSourceSet))
        }
        extension.options.convention(mapOf("VERSION" to project.version.toString()))
    }

    private fun addBuildMarkToSourceSets(
        sourceSets: List<KotlinSourceSet>,
        outputDirectory: Provider<Directory>
    )
    {
        sourceSets.forEach { it.kotlin.srcDir(outputDirectory) }
    }

    companion object
    {
        /** Output directory relative to the Gradle build directory. */
        const val OUTPUT_DIR = "generated/buildmark/"
    }
}