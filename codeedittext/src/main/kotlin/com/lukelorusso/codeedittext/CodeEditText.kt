package com.lukelorusso.codeedittext

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.layout_code_edit_text.view.*

class CodeEditText constructor(context: Context, attrs: AttributeSet) :
        FrameLayout(context, attrs) {

    init {
        inflate(context, R.layout.layout_code_edit_text, this)
    }

    companion object {
        private const val LENGTH_CODE_COMPLETE = 6
    }

    private var code: String = ""
    private var onCodeChangedListener: ((Pair<String, Boolean>) -> Unit)? = null

    fun setOnCodeChangedListener(listener: ((Pair<String, Boolean>) -> Unit)?) {
        this.onCodeChangedListener = listener
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode) {
            editCodeReal.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (before > 0) {
                        // Remove last char
                        if (code.isNotEmpty()) {
                            code = code.substring(0, code.length - 1)
                        }
                    } else if (code.length < LENGTH_CODE_COMPLETE && s.isNotEmpty()) {
                        val unicodeChar = s[s.length - 1]
                        code += unicodeChar
                    }

                    showCode()
                    notifyCodeChanged()
                }

                override fun afterTextChanged(s: Editable) {}
            })

            layoutCode.setOnClickListener { editCodeReal.showKeyboard() }
        }
    }

    private fun showCode() {
        textViewCode1.text = ""
        textViewCode2.text = ""
        textViewCode3.text = ""
        textViewCode4.text = ""
        textViewCode5.text = ""
        textViewCode6.text = ""

        if (code.isNotEmpty()) {
            textViewCode1.text = code.getLetterAt(0)
            if (code.length >= 2) {
                textViewCode2.text = code.getLetterAt(1)
                if (code.length >= 3) {
                    textViewCode3.text = code.getLetterAt(2)
                    if (code.length >= 4) {
                        textViewCode4.text = code.getLetterAt(3)
                        if (code.length >= 5) {
                            textViewCode5.text = code.getLetterAt(4)
                            if (code.length >= 6) {
                                textViewCode6.text = code.getLetterAt(5)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun notifyCodeChanged(): Boolean = (code.length == LENGTH_CODE_COMPLETE).apply {
        onCodeChangedListener?.invoke(Pair(code, this))
    }

    private fun String.getLetterAt(position: Int): String = this[position].toString()

    private fun View.showKeyboard() {
        requestFocus()
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.also {
            it.showSoftInput(this, InputMethodManager.SHOW_FORCED)
        }
    }

}
