package com.example.calculator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import javax.script.ScriptEngineManager

class MainActivity : AppCompatActivity() {

    // reason: https://developer.android.com/topic/libraries/architecture/viewmodel.html#sharing
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel = ScriptEngineManager().getEngineByName("rhino")
            .let { engine ->
                ViewModelProvider(
                viewModelStore,
                MainViewModel.Factory(engine)
            ).get(MainViewModel::class.java) }
        addFragments()
    }

    private fun addFragments() {
        // FIXME overdraw when using add fragment (use Layout Inspector)
        supportFragmentManager.beginTransaction().run {
            replace(container1.id, ResultFragment())
            replace(container2.id, KeyboardFragment())
        }.commit()
    }
}