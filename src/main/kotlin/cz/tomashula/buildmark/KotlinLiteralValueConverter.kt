package cz.tomashula.buildmark

import com.squareup.kotlinpoet.CodeBlock
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/**
 * Converts a value of a supported type to a Kotlin code literal of that value.
 * @see convert
 */
class KotlinLiteralValueConverter
{
    private val convertors = mutableMapOf<KClass<*>, (Any) -> CodeBlock>()
    
    private fun registerConvertor(clazz: KClass<*>, convertor: (Any) -> CodeBlock) = convertors.put(clazz, convertor)
    
    private fun registerDirectLiteralConverters()
    {
        setOf<KClass<*>>(
            Int::class,
            Double::class,
            Boolean::class
        ).forEach { literalType -> registerConvertor(literalType) { CodeBlock.of("%L", it) } }
        
        // Cast to Int and use Int's converter
        setOf<KClass<*>>(
            Byte::class,
            Short::class
        ).forEach { type -> registerConvertor(type) { CodeBlock.of(convert((it as Number).toInt())) } }
    }
    
    private fun registerUnsignedConverters()
    {
        setOf<KClass<*>>(
            UInt::class,
            UShort::class,
            ULong::class,
            UByte::class
        ).forEach { uType -> registerConvertor(uType) { CodeBlock.of("%Lu", it) } }
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
        if (value == null)
            return "null"

        val type = value::class

        val converterForTypeOrSupertype = convertors.entries.find { type.isSubclassOf(it.key) }?.value
        
        return converterForTypeOrSupertype?.invoke(value)?.toString() 
            ?: throw IllegalArgumentException("Unsupported type: ${value::class}")
    }
    
    init
    {
        registerDirectLiteralConverters()
        registerUnsignedConverters()
        registerConvertor(String::class) { CodeBlock.of("%S", it) }
        registerConvertor(Char::class) { CodeBlock.of("'%L'", it) }
        registerConvertor(Float::class) { CodeBlock.of("%Lf", it) }
        registerConvertor(Long::class) { CodeBlock.of("%LL", it) }
        registerConvertor(ArrayList::class) { list ->
            val elements = (list as List<*>).map { convert(it) }
            CodeBlock.of("listOf(%L)", elements.joinToString(", "))
        }
    }
}