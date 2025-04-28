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
import kotlin.test.assertTrue


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
        testValue(null)
    }
    
    @Test
    fun testInt()
    {
        testValue(Random.nextInt())
    }

    @Test
    fun testByte()
    {
        testValue(Random.nextInt().toByte())
    }

    @Test
    fun testShort()
    {
        testValue(Random.nextInt().toShort())
    }

    @Test
    fun testLong()
    {
        testValue(Random.nextLong())
    }

    @Test
    fun testDouble()
    {
        testValue(Random.nextDouble())
    }

    @Test
    fun testBoolean()
    {
        testValue(Random.nextBoolean())
    }

    @Test
    fun testString()
    {
        val randomString = String(Random.nextBytes(20), Charset.forName("UTF-8"))
        testValue(randomString)
    }

    @Test
    fun testChar()
    {
        val randomChar = Random.nextInt().toChar()
        testValue(randomChar)
    }

    @Test
    fun testFloat()
    {
        val randomFloat = Random.nextFloat()
        testValue(randomFloat)
    }

    @Test
    fun testList()
    {
        val randomList = List(Random.nextInt(10, 20)) { Random.nextInt() }
        testValue(randomList)
    }

    @Test
    fun testUnsupportedType()
    {
        assertFailsWith<IllegalArgumentException> { testValue(object {}) }
    }

    private fun testValue(value: Any?)
    {
        val valueCode = generateCodeEvaluatingToValue(value)
        println("Generated test code block: \n$valueCode")
        val valueEvaluatedFromCode = eval(valueCode)
        assertEquals(value, valueEvaluatedFromCode, "Generated test code block: \n$valueCode")
    }
    
    private fun generateCodeEvaluatingToValue(value: Any?): String
    {
        val valueLiteral = converter.convert(value)
        val valueLiteralEvaluationCode = valueLiteral

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
