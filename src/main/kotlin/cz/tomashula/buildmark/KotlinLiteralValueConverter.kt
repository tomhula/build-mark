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
    
    private fun <T : Any> registerConvertor(clazz: KClass<T>, convertor: (T) -> CodeBlock) = convertors.put(clazz, convertor as (Any) -> CodeBlock)

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

        setOf<KClass<*>>(
            UInt::class,
            UShort::class,
            ULong::class,
            UByte::class
        ).forEach<KClass<*>> { uType -> registerConvertor(uType) { CodeBlock.of("%Lu", it) } }
        
        registerConvertor(String::class) { CodeBlock.of("%S", it) }
        registerConvertor(Char::class) { CodeBlock.of("'%L'", it) }
        registerConvertor(Float::class) { CodeBlock.of("%Lf", it) }
        registerConvertor(Long::class) { CodeBlock.of("%LL", it) }
        registerConvertor(List::class) { list ->
            val elements = list.map { convert(it) }
            CodeBlock.of("listOf(%L)", elements.joinToString(", "))
        }
        registerConvertor(Set::class) { set ->
            val elements = set.map { convert(it) }
            CodeBlock.of("setOf(%L)", elements.joinToString(", "))
        }
        registerConvertor(Map::class) { map ->
            val entries = map.entries.map { "${convert(it.key)} to ${convert(it.value)}" }
            CodeBlock.of("mapOf(%L)", entries.joinToString(", "))
        }
        registerConvertor(Pair::class) { pair ->
            val (first, second) = pair
            CodeBlock.of("%L to %L", convert(first), convert(second))
        }
        registerConvertor(Array::class) { array ->
            val elements = array.map { convert(it) }
            CodeBlock.of("arrayOf(%L)", elements.joinToString(", "))
        }

        // Primitive array converters
        registerConvertor(IntArray::class) { array ->
            val elements = array.joinToString(", ", transform = ::convert)
            CodeBlock.of("intArrayOf(%L)", elements)
        }
        registerConvertor(ByteArray::class) { array ->
            val elements = array.joinToString(", ", transform = ::convert)
            CodeBlock.of("byteArrayOf(%L)", elements)
        }
        registerConvertor(ShortArray::class) { array ->
            val elements = array.joinToString(", ", transform = ::convert)
            CodeBlock.of("shortArrayOf(%L)", elements)
        }
        registerConvertor(LongArray::class) { array ->
            val elements = array.joinToString(", ", transform = ::convert)
            CodeBlock.of("longArrayOf(%L)", elements)
        }
        registerConvertor(FloatArray::class) { array ->
            val elements = array.joinToString(", ", transform = ::convert)
            CodeBlock.of("floatArrayOf(%L)", elements)
        }
        registerConvertor(DoubleArray::class) { array ->
            val elements = array.joinToString(", ", transform = ::convert)
            CodeBlock.of("doubleArrayOf(%L)", elements)
        }
        registerConvertor(BooleanArray::class) { array ->
            val elements = array.joinToString(", ", transform = ::convert)
            CodeBlock.of("booleanArrayOf(%L)", elements)
        }
        registerConvertor(CharArray::class) { array ->
            val elements = array.joinToString(", ") { "'$it'" }
            CodeBlock.of("charArrayOf(%L)", elements)
        }
    }
}
