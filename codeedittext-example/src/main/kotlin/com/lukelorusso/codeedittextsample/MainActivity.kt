package com.lukelorusso.codeedittextsample

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //cetMyCode.maxLength = 6
        //cetMyCode.text = "1234"
        //cetMyCode.inputType = InputType.TYPE_CLASS_TEXT
        //cetMyCode.maskTheCode = true
        //cetMyCode.codeMaskChar = '#'
        cetMyCode.setOnCodeChangedListener { (code, completed) ->
            Log.d("CodeEditText", code)
            if (completed) hideKeyboard()
        }
    }

    private fun Activity.hideKeyboard() = currentFocus?.also { it.hideKeyboard() }

    private fun View.hideKeyboard() =
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.also {
            it.hideSoftInputFromWindow(windowToken, 0)
        }

}
