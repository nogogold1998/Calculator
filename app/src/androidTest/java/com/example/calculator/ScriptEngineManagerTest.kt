package com.example.calculator

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

class ScriptEngineManagerAndroidTest {

    lateinit var engine: ScriptEngine

    @Before
    fun setup() {
        engine = ScriptEngineManager().getEngineByName("rhino")
    }

    @Test
    fun plusNumbers() {
        // given
        val expressions = mapOf("40+2" to "42.0", "40 + 2" to "42.0", "2+4+5+7" to "18.0")
        // when
        for ((expression, answer) in expressions) {
            assertThat(engine.eval(expression).toString(), equalTo(answer))
        }
    }

    @Test
    fun complexExpression() {
        // given
        val expressions = mapOf(
            "40 + 2 * 4" to 48.0,
            "(2 -4) * 8" to -16.0,
            "(6 + 6) / 3" to 4.0,
            "(6 + 4) / 3" to 10.0 / 3
        )
        // when
        for((expression, answer) in expressions){
            assertThat(engine.eval(expression).toString().toDoubleOrNull() ?: 0.0, `is`(answer))
        }
    }

    @Test
    fun implicitMultipleExpression(){
        // given
        val expression = "3 * ((2 + 4))"
        val actual = "18.0"

        assertThat(engine.eval(expression).toString(), `is`(actual))
    }
}