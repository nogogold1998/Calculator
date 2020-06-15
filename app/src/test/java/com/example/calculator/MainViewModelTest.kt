package com.example.calculator

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.script.ScriptEngineManager

class MainViewModelTest {
    lateinit var subject: MainViewModel

    @Rule
    @JvmField
    val instantTaskRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        subject = MainViewModel(ScriptEngineManager().getEngineByName("JavaScript"))
    }

    @Test
    fun `simple expression`() {
        // when
        subject.run {
            onClickNum(1)
            onClickNum(2)
            onClickOperator('+')
            onClickNum(8)
        }
        assertThat(subject.expression.getOrAwaitValue(), equalTo("12+8"))
        assertThat(subject.result.getOrAwaitValue(), equalTo("20"))
    }

    @Test
    fun `parentheses test`(){
        subject.run {
            onClickNum(3)
            onClickOperator('*')
            onOpen()
            onClickNum(3)
            onClickOperator('+')
            onClickNum(4)
        }
        assertThat(subject.expression.getOrAwaitValue(), equalTo("3*(3+4"))
        subject.run {
            onClose()
            onClose()
        }
        assertThat(subject.expression.getOrAwaitValue(), equalTo("3*(3+4)"))
        assertThat(subject.result.getOrAwaitValue(), equalTo("21"))
    }

    @Test
    fun `backspace then clear then force equal`(){
        subject.run {
            onClickNum(3)
            onClickOperator('*')
            onOpen()
        }
        assertThat(subject.expression.getOrAwaitValue(), equalTo("3*("))
        subject.onBackspace()
        assertThat(subject.expression.getOrAwaitValue(), equalTo("3*"))
        subject.onClear()
        assertThat(subject.expression.getOrAwaitValue(), equalTo(""))
        subject.onEqual()
        assertThat(subject.expression.getOrAwaitValue(), equalTo(""))
    }

    @Test
    fun `force Equal`(){
        subject.run {
            onClickNum(3)
            onClickOperator('*')
            onOpen()
            onOpen()
            onClickNum(3)
            onClickOperator('+')
            onClickNum(4)
        }
        assertThat(subject.expression.getOrAwaitValue(), equalTo("3*((3+4"))
        subject.onEqual()
        assertThat(subject.expression.getOrAwaitValue(), equalTo("3*((3+4))"))
        assertThat(subject.result.getOrAwaitValue(), equalTo("21"))
    }
}

fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    this.observeForever(observer)

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}