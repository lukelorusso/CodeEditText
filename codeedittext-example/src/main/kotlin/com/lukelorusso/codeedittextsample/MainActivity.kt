package com.lukelorusso.codeedittextsample

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.lukelorusso.codeedittextsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityMainBinding.inflate(layoutInflater).also { inflated ->
            binding = inflated
            setContentView(binding.root)
        }

        binding.apply {
            //cetMyCode.maxLength = 6
            //cetMyCode.text = "1234"
            //cetMyCode.inputType = InputType.TYPE_CLASS_TEXT
            //cetMyCode.codeMaskChar = '#'
            //cetMyCode.codePlaceholder = '-'
            //cetMyCode.maskTheCode = true
            //cetMyCode.scrollDurationInMillis = 300
            cetMyCode.setOnCodeChangedListener { (code, completed) ->
                Log.d("CodeEditText", "text: \"$code\"")

                if (completed)
                    hideKeyboard()
            }
        }
    }

    private fun Activity.hideKeyboard() = currentFocus?.also { it.hideKeyboard() }

    private fun View.hideKeyboard() =
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.also {
            it.hideSoftInputFromWindow(windowToken, 0)
        }

}
