@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.lukelorusso.codeedittext

import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.layout_code_edit_text.view.*


class CodeEditText constructor(context: Context, attrs: AttributeSet) :
    FrameLayout(context, attrs) {

    companion object {
        private const val DEFAULT_CODE_LENGTH = 4
        private const val DEFAULT_CODE_MASK_CHAR = '•'
    }

    var codeMaskChar: Char = DEFAULT_CODE_MASK_CHAR
        set(value) {
            field = value
            text = text
        }

    var inputType: Int
        get() = editCodeReal.inputType
        set(value) {
            editCodeReal.inputType = value
        }

    var maskTheCode: Boolean = false
        set(value) {
            field = value
            text = text
        }

    var maxLength: Int = DEFAULT_CODE_LENGTH
        set(value) {
            field = value
            if (initEnded) onAttachedToWindow()
        }

    var text: Editable = "".toEditable()
        set(value) {
            field = value
            if (initEnded) renderCode()
            else rememberToRenderCode = true
        }

    private var onCodeChangedListener: ((Pair<String, Boolean>) -> Unit)? = null
    private var initEnded =
        false // if true allows the view to be updated after setting an attribute programmatically
    private var rememberToRenderCode = false

    fun setOnCodeChangedListener(listener: ((Pair<String, Boolean>) -> Unit)?) {
        this.onCodeChangedListener = listener
    }

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        inflate(context, R.layout.layout_code_edit_text, this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CodeEditText, 0, 0)
        try {
            // codeMaskChar
            attributes.getString(R.styleable.CodeEditText_cet_codeMaskChar)?.also {
                codeMaskChar = it[0]
            }

            // inputType
            if (attributes.hasValue(R.styleable.CodeEditText_android_inputType))
                editCodeReal.inputType =
                    attributes.getInt(R.styleable.CodeEditText_android_inputType, 0)

            // maskTheCode
            if (attributes.hasValue(R.styleable.CodeEditText_cet_maskTheCode)) maskTheCode =
                attributes.getBoolean(R.styleable.CodeEditText_cet_maskTheCode, false)

            // maxLength
            maxLength = attributes.getInt(R.styleable.CodeEditText_android_maxLength, maxLength)

            // text
            attributes.getString(R.styleable.CodeEditText_android_text)
                ?.also { text = it.toEditable() }

        } finally {
            attributes.recycle()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initEnded = true

        if (!isInEditMode) {
            llCodeWrapper.removeAllViews()
            for (i in 0 until maxLength) {
                View.inflate(
                    context,
                    R.layout.item_code_edit_text,
                    findViewById(R.id.llCodeWrapper)
                )
            }

            editCodeReal.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
            editCodeReal.removeTextChangedListener(textChangedListener)
            editCodeReal.addTextChangedListener(textChangedListener)

            llCodeWrapper.setOnClickListener { editCodeReal.showKeyboard() }
        }

        if (rememberToRenderCode) {
            rememberToRenderCode = false
            post { renderCode() }
        }
    }

    private val textChangedListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            text = s
        }
    }

    private fun renderCode() {
        for (i in 0 until llCodeWrapper.childCount) {
            val itemContainer = llCodeWrapper.getChildAt(i)

            itemContainer.getTextView().text =
                if (text.length > i)
                    if (maskTheCode) codeMaskChar.toString() else text[i].toString()
                else ""

            if (i == text.length - 1 && !itemContainer.isFullyVisibleInside(hsvCodeWrapperScroller))
                hsvCodeWrapperScroller.focusOnView(itemContainer)
        }
        notifyCodeChanged()
    }

    private fun View.isFullyVisibleInside(parentView: View): Boolean {
        val scrollBounds = Rect()
        parentView.getDrawingRect(scrollBounds)
        val left = this.x
        val right = left + this.width
        val top = this.y
        val bottom = top + this.height
        return scrollBounds.left < left &&
                scrollBounds.right > right &&
                scrollBounds.top < top &&
                scrollBounds.bottom > bottom
    }

    private fun View.focusOnView(childView: View) =
        post { scrollTo(childView.left, childView.top) }

    private fun View.getTextView(): TextView = findViewById(R.id.tvCode)

    private fun notifyCodeChanged(): Boolean = (text.length == maxLength).apply {
        onCodeChangedListener?.invoke(Pair(text.toString(), this))
    }

    private fun String.toEditable(): Editable =
        Editable.Factory.getInstance().newEditable(this)

    private fun View.showKeyboard() {
        requestFocus()
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.also {
            it.showSoftInput(this, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
    }

}
