package cz.tomashula.buildmark

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

class BuildMark : Plugin<Project>
{
    override fun apply(project: Project)
    {
        val outputDirectory = project.layout.buildDirectory.dir(OUTPUT_DIR)
        
        val extension = project.extensions.create<BuildMarkExtension>("buildMark")

        addBuildMarkToSourceSets(project, outputDirectory)

        extension.targetPackage.convention("")
        extension.targetObjectName.convention("BuildMark")
        extension.options.convention(mapOf("VERSION" to project.version.toString()))

        val generateTask = project.tasks.register<GenerateBuildMarkTask>("generateBuildMark") {
            group = "build"
            description = "Generates the build mark object"
            outputDir.set(outputDirectory)
        }
        

        project.tasks.named("compileKotlin").configure {
            dependsOn(generateTask)
        }
        
        project.afterEvaluate {
            generateTask.get().generate()
        }
    }

    private fun addBuildMarkToSourceSets(
        project: Project,
        outputDirectory: Provider<Directory>
    )
    {
        project.plugins.withId("org.jetbrains.kotlin.jvm") {
            val kotlin = project.extensions.getByName("kotlin") as KotlinProjectExtension
            kotlin.sourceSets.getByName("main").kotlin.srcDir(outputDirectory)
        }
    }

    companion object
    {
        /** Output directory relative to the Gradle build directory. */
        const val OUTPUT_DIR = "generated/buildmark/"
    }
}