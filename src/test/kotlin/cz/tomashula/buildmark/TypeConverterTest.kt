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
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class TypeConverterTest
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
    }

    @Test
    fun testUnsupportedType()
    {
        assertFailsWith<IllegalArgumentException> { assertValueEqualsEvaluated(object {}) }
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
