package cz.tomashula.buildmark

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

internal abstract class GenerateBuildMarkTask : DefaultTask()
{
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    private val converter = KotlinLiteralValueConverter()

    @TaskAction
    fun generate()
    {
        val buildMarkExt = project.extensions.getByType(BuildMarkExtension::class.java)
        
        val outputDirectory = outputDir.get().asFile
        val targetObjectName = buildMarkExt.targetObjectName.get()
        val targetPackage = buildMarkExt.targetPackage.get()

        // KotlinPoet is not used because it currently does not support type inference
        
        val properties = buildMarkExt.options.get().map { option ->
            converter.convert(option.value).let { value -> "val ${option.key} = $value" }
        }
        
        val code = buildString { 
            if (targetPackage.isNotBlank())
            {
                appendLine("package $targetPackage")
                appendLine()
            }
                
            appendLine("object $targetObjectName")
            appendLine("{")
            properties.forEach { appendLine("    $it") }
            appendLine("}")
        }
        
        outputDirectory.deleteRecursively()
        
        val packageAsPath = targetPackage.replace(".", "/")
        val outputFile = outputDirectory.resolve(packageAsPath).resolve("$targetObjectName.kt")
        outputFile.parentFile.mkdirs()
        outputFile.writeText(code)
    }
}