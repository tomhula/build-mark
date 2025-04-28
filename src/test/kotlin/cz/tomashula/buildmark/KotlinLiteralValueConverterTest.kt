package cz.tomashula.buildmark

import com.squareup.kotlinpoet.CodeBlock
import org.junit.jupiter.api.Test
import java.nio.charset.Charset
import kotlin.random.Random
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.valueOrThrow
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class KotlinLiteralValueConverterTest
{
    companion object
    {
        private val scriptHost = BasicJvmScriptingHost()
        private val scriptCompilationConfiguration = ScriptCompilationConfiguration {}
        private val scriptEvaluationConfiguration = ScriptEvaluationConfiguration {}
    }

    private val converter = KotlinLiteralValueConverter()

    @Test
    fun testNull()
    {
        assertValueEqualsEvaluated(null)
    }

    @Test
    fun testInt()
    {
        assertValueEqualsEvaluated(Random.nextInt())
    }

    @Test
    fun testByte()
    {
        val randomByte = Random.nextInt().toByte()
        val evaluated = evaluateValue(randomByte)
        assertEquals(randomByte.toInt(), evaluated)
    }

    @Test
    fun testShort()
    {
        val randomShort = Random.nextInt().toShort()
        val evaluated = evaluateValue(randomShort)
        assertEquals(randomShort.toInt(), evaluated)
    }

    @Test
    fun testLong()
    {
        assertValueEqualsEvaluated(Random.nextLong())
    }

    @Test
    fun testDouble()
    {
        assertValueEqualsEvaluated(Random.nextDouble())
    }

    @Test
    fun testBoolean()
    {
        assertValueEqualsEvaluated(Random.nextBoolean())
    }

    @Test
    fun testString()
    {
        val randomString = String(Random.nextBytes(20), Charset.forName("UTF-8"))
        assertValueEqualsEvaluated(randomString)
    }

    @Test
    fun testChar()
    {
        val randomChar = Random.nextInt().toChar()
        assertValueEqualsEvaluated(randomChar)
    }

    @Test
    fun testFloat()
    {
        val randomFloat = Random.nextFloat()
        assertValueEqualsEvaluated(randomFloat)
    }

    @Test
    fun testList()
    {
        val randomList = List(Random.nextInt(10, 20)) { Random.nextInt() }
        assertValueEqualsEvaluated(randomList)

        val mixedList = listOf(1, "Hello", 2.5f, true)
        assertValueEqualsEvaluated(mixedList)
    }

    @Test
    fun testSet()
    {
        val randomSet = (1..Random.nextInt(5, 10)).map { Random.nextInt() }.toSet()
        assertValueEqualsEvaluated(randomSet)

        val mixedSet = setOf(1, "Hello", 2.5f, true)
        assertValueEqualsEvaluated(mixedSet)
    }

    @Test
    fun testMap()
    {
        val randomMap = (1..Random.nextInt(5, 10)).associate { it to Random.nextInt() }
        assertValueEqualsEvaluated(randomMap)

        val mixedMap = mapOf(1 to "One", "Two" to 2, 3.5f to true)
        assertValueEqualsEvaluated(mixedMap)
    }

    @Test
    fun testPair()
    {
        val randomPair = Random.nextInt() to Random.nextDouble()
        assertValueEqualsEvaluated(randomPair)

        val mixedPair = "Hello" to 42
        assertValueEqualsEvaluated(mixedPair)
    }

    @Test
    fun testArray()
    {
        val randomArray = Array(Random.nextInt(5, 10)) { Random.nextInt() }
        val randomArrayEvaluated = evaluateValue(randomArray)
        assertContentEquals(randomArray, randomArrayEvaluated as Array<Int>)

        val mixedArray: Array<Any?> = arrayOf(1, "Hello", 2.5f, true, null)
        val mixedArrayEvaluated = evaluateValue(mixedArray)
        assertContentEquals(mixedArray, mixedArrayEvaluated as Array<Any?>)
    }

    @Test
    fun testIntArray()
    {
        val randomIntArray = IntArray(Random.nextInt(5, 10)) { Random.nextInt() }
        val randomIntArrayEvaluated = evaluateValue(randomIntArray)
        assertContentEquals(randomIntArray, randomIntArrayEvaluated as IntArray)
    }

    @Test
    fun testByteArray()
    {
        val randomByteArray = ByteArray(Random.nextInt(5, 10)) { Random.nextInt().toByte() }
        val randomByteArrayEvaluated = evaluateValue(randomByteArray)
        assertContentEquals(randomByteArray, randomByteArrayEvaluated as ByteArray)
    }

    @Test
    fun testShortArray()
    {
        val randomShortArray = ShortArray(Random.nextInt(5, 10)) { Random.nextInt().toShort() }
        val randomShortArrayEvaluated = evaluateValue(randomShortArray)
        assertContentEquals(randomShortArray, randomShortArrayEvaluated as ShortArray)
    }

    @Test
    fun testLongArray()
    {
        val randomLongArray = LongArray(Random.nextInt(5, 10)) { Random.nextLong() }
        val randomLongArrayEvaluated = evaluateValue(randomLongArray)
        assertContentEquals(randomLongArray, randomLongArrayEvaluated as LongArray)
    }

    @Test
    fun testFloatArray()
    {
        val randomFloatArray = FloatArray(Random.nextInt(5, 10)) { Random.nextFloat() }
        val randomFloatArrayEvaluated = evaluateValue(randomFloatArray)
        assertContentEquals(randomFloatArray, randomFloatArrayEvaluated as FloatArray)
    }

    @Test
    fun testDoubleArray()
    {
        val randomDoubleArray = DoubleArray(Random.nextInt(5, 10)) { Random.nextDouble() }
        val randomDoubleArrayEvaluated = evaluateValue(randomDoubleArray)
        assertContentEquals(randomDoubleArray, randomDoubleArrayEvaluated as DoubleArray)
    }

    @Test
    fun testBooleanArray()
    {
        val randomBooleanArray = BooleanArray(Random.nextInt(5, 10)) { Random.nextBoolean() }
        val randomBooleanArrayEvaluated = evaluateValue(randomBooleanArray)
        assertContentEquals(randomBooleanArray, randomBooleanArrayEvaluated as BooleanArray)
    }

    @Test
    fun testCharArray()
    {
        val randomCharArray = CharArray(Random.nextInt(5, 10)) { Random.nextInt().toChar() }
        val randomCharArrayEvaluated = evaluateValue(randomCharArray)
        assertContentEquals(randomCharArray, randomCharArrayEvaluated as CharArray)
    }

    @Test
    fun testUnsupportedType()
    {
        assertFailsWith<IllegalArgumentException> {
            assertValueEqualsEvaluated(object
            {})
        }
    }

    private fun assertValueEqualsEvaluated(value: Any?)
    {
        val valueEvaluatedFromCode = evaluateValue(value)
        assertEquals(value, valueEvaluatedFromCode)
    }

    private fun evaluateValue(value: Any?): Any?
    {
        val valueCode = generateCodeEvaluatingToValue(value)
        return eval(valueCode)
    }

    private fun generateCodeEvaluatingToValue(value: Any?): String
    {
        val valueLiteral = converter.convert(value)
        val valueLiteralEvaluationCode = if (value != null)
            valueLiteral
        else
        // BUG: `null` evaluates to Unit for some reason. However a nullable variable correctly evaluates to null. 
            CodeBlock.builder()
                .addStatement("val value: Any? = null")
                .addStatement("value")
                .build().toString()

        return valueLiteralEvaluationCode
    }

    private fun eval(kotlinCode: String): Any?
    {
        val evaluationResult = scriptHost.eval(
            kotlinCode.toScriptSource(),
            scriptCompilationConfiguration,
            scriptEvaluationConfiguration
        ).valueOrThrow()

        val returnValue = evaluationResult.returnValue

        return when (returnValue)
        {
            is ResultValue.Value -> returnValue.value
            is ResultValue.Unit -> Unit
            is ResultValue.Error -> throw returnValue.error
            is ResultValue.NotEvaluated -> throw IllegalStateException("Script was not evaluated")
        }
    }
}
