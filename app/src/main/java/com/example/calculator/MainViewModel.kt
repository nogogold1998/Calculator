package com.example.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.script.ScriptEngine
import javax.script.ScriptException

class MainViewModel(private val engine: ScriptEngine) :
    ViewModel() {
     val expression = MutableLiveData<String>().apply { value = "" }

    // val expression: LiveData<String>
    //     get() = _expression
    private lateinit var previousResult: String

    val result: LiveData<String> = Transformations.map(expression) { expression ->
        try {
            (engine.eval(expression) ?: "0").toString().also { previousResult = it}
        } catch (e: ScriptException) {
            previousResult
        }
    }

    fun onClickNum(num: Int) {
        expression.value += num
    }

    fun onClickOperator(operator: Char) {
        if (operator in setOf('+', '-', '*', '/'))
            expression.value += operator
    }

    fun onOpen() {
        expression.value += '('
    }

    fun onClose() {
        if (expression.value?.count { it == '(' } ?: 0 > expression.value?.count { it == ')' } ?: 0) {
            expression.value += ')'
        }
    }

    fun onBackspace() {
        if (expression.value != null) {
            expression.value = expression.value!!.dropLast(1)
        }
    }

    fun onClear() {
        expression.value = ""
    }

    fun onEqual() {
        (expression.value!!.count { it == '(' } - expression.value!!.count { it == ')' }).let {
            expression.value = expression.value + (1..it).fold("") { s: String, _: Int -> "$s)" }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val engine: ScriptEngine) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(engine) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}