package cz.tomashula.buildmark

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.jvm.isAccessible

internal abstract class GenerateBuildMarkTask : DefaultTask()
{
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty
    
    @TaskAction
    fun generate()
    {
        val buildMarkExt = project.extensions.getByType(BuildMarkExtension::class.java)
        
        val generatePropertyFunction = this::class.declaredMemberFunctions.first { it.name == "generateProperty" }
        generatePropertyFunction.isAccessible = true
        
        val properties = buildMarkExt.options.get().map { option ->
            generatePropertyFunction.call(this, option.key, option.value::class, option.value) as PropertySpec
        }
        
        val buildMarkObject = TypeSpec.objectBuilder(buildMarkExt.targetObjectName.get())
            .addProperties(properties)
            .build()
        
        val file = FileSpec.builder(buildMarkExt.targetPackage.get(), buildMarkExt.targetObjectName.get())
            .addType(buildMarkObject)
            .build()
        
        file.writeTo(outputDir.get().asFile)
    }
    
    private fun <T : Any> generateProperty(name: String, clazz: KClass<T>, value: T): PropertySpec
    {
        val isConst = primitiveTypes.any { clazz == it }

        return PropertySpec.builder(name, clazz).apply { 
            if (isConst)
                addModifiers(KModifier.CONST)
            when(clazz)
            {
                String::class -> initializer("%S", value)
                Float::class -> initializer("%Lf", value)
                else -> initializer("%L", value)
            }
                
        }.build()
    }
    
    companion object
    {
        /** Primitive types, that support the `const` keyword. */
        val primitiveTypes = listOf(String::class, Int::class, Long::class, Float::class, Double::class)
    }
}