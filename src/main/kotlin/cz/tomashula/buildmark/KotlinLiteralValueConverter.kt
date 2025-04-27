package cz.tomashula.buildmark

import com.squareup.kotlinpoet.CodeBlock
import kotlin.reflect.KClass

/**
 * Converts value of a supported type to a Kotlin code literal of that value.
 * @see convert
 */
class KotlinLiteralValueConverter
{
    private val convertors = mutableMapOf<KClass<*>, (Any) -> String>()
    
    private fun registerConvertor(clazz: KClass<*>, convertor: (Any) -> String) = convertors.put(clazz, convertor)
    
    private fun registerDirectLiteralConverters()
    {
        listOf<KClass<*>>(
            Int::class,
            Short::class,
            Long::class,
            Byte::class,
            Double::class,
            Boolean::class
        ).forEach { literalType -> registerConvertor(literalType) { CodeBlock.of("%L", it).toString() } }
    }
    
    private fun registerUnsignedConverters()
    {
        listOf<KClass<*>>(
            UInt::class,
            UShort::class,
            ULong::class,
            UByte::class
        ).forEach { uType -> registerConvertor(uType) { CodeBlock.of("%Lu", it).toString() } }
    }

    /**
     * Converts [value] of supported type to a Kotlin code that evaluates back to that value.
     * Example:
     * ```kotlin
     * convert("Hello") // "Hello"
     * convert(2.8f) // 2.8f
     * val list = listOf("Hello", "World")
     * convert(list) // listOf("Hello", "World")
     * ```
     * @throws IllegalArgumentException for [value] of unsupported type.
     */
    fun convert(value: Any?): String
    {
        return if (value == null) 
            "null"
        else
            convertors[value::class]?.invoke(value) ?: throw IllegalArgumentException("Unsupported type: ${value::class}")
    }
    
    init
    {
        registerDirectLiteralConverters()
        registerUnsignedConverters()
        registerConvertor(String::class) { CodeBlock.of("%S", it).toString() }
        registerConvertor(Char::class) { CodeBlock.of("'%L'", it).toString() }
        registerConvertor(Float::class) { CodeBlock.of("%Lf", it).toString() }
        registerConvertor(ArrayList::class) { list ->
            val elements = (list as List<*>).map { convert(it) }
            CodeBlock.of("listOf(%L)", elements.joinToString(", ")).toString()
        }
    }
}