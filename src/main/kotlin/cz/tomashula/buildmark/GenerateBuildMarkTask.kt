package cz.tomashula.buildmark

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import kotlin.reflect.KClass

internal abstract class GenerateBuildMarkTask : DefaultTask()
{
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate()
    {
        val buildMarkExt = project.extensions.getByType(BuildMarkExtension::class.java)

        val properties = buildMarkExt.options.get().map { option ->
            generateProperty(option.key, option.value::class, option.value.toString())
        }

        val buildMarkObject = TypeSpec.objectBuilder(buildMarkExt.targetObjectName.get())
            .addProperties(properties)
            .build()

        val file = FileSpec.builder(buildMarkExt.targetPackage.get(), buildMarkExt.targetObjectName.get())
            .addType(buildMarkObject)
            .build()

        file.writeTo(outputDir.get().asFile)
    }

    private fun <T : Any> generateProperty(name: String, clazz: KClass<T>, value: String): PropertySpec
    {
        val resultClass = clazz.takeIf { it in allPrimitiveTypes } ?: String::class

        return PropertySpec.builder(name, resultClass).apply {
            when (clazz)
            {
                Float::class -> initializer("%Lf", value)
                Char::class -> initializer("'%L'", value)
                in unsignedTypes -> initializer("%Lu", value)
                in directLiteralTypes -> initializer("%L", value)
                else -> initializer("%S", value)
            }

        }.build()
    }

    companion object
    {
        val allPrimitiveTypes = listOf<KClass<*>>(
            Int::class,
            Short::class,
            Long::class,
            Byte::class,
            Float::class,
            Double::class,
            Boolean::class,
            Char::class,
            UInt::class,
            UShort::class,
            ULong::class,
            UByte::class
        )

        /** All types, whose value as a string can be placed as literal as is. */
        val directLiteralTypes = listOf<KClass<*>>(
            Int::class,
            Short::class,
            Long::class,
            Byte::class,
            Double::class,
            Boolean::class,
        )

        val unsignedTypes = listOf<KClass<*>>(
            UInt::class,
            UShort::class,
            ULong::class,
            UByte::class
        )
    }
}