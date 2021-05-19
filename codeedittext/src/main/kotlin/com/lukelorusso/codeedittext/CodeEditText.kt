@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.lukelorusso.codeedittext

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import com.lukelorusso.codeedittext.databinding.LayoutCodeEditTextBinding


class CodeEditText constructor(context: Context, attrs: AttributeSet) :
    FrameLayout(context, attrs) {

    companion object {
        private const val DEFAULT_CODE_LENGTH = 4
        private const val DEFAULT_CODE_MASK_CHAR = '•'
        private const val DEFAULT_CODE_PLACEHOLDER = ' '
        private const val DEFAULT_SCROLL_DURATION_IN_MILLIS = 250
    }

    private var binding: LayoutCodeEditTextBinding =
        LayoutCodeEditTextBinding.inflate(LayoutInflater.from(context))

    var codeMaskChar: Char = DEFAULT_CODE_MASK_CHAR
        set(value) {
            field = value
            editable = editable
        }

    var codePlaceholder: Char = DEFAULT_CODE_PLACEHOLDER
        set(value) {
            field = value
            editable = editable
        }

    var inputType: Int
        get() = binding.editCodeReal.inputType
        set(value) {
            binding.editCodeReal.inputType = value
        }

    var maskTheCode: Boolean = false
        set(value) {
            field = value
            editable = editable
        }

    var maxLength: Int = DEFAULT_CODE_LENGTH
        set(value) {
            field = value
            if (initEnded) onAttachedToWindow()
        }

    var scrollDurationInMillis: Int = DEFAULT_SCROLL_DURATION_IN_MILLIS

    private var onCodeChangedListener: ((Pair<String, Boolean>) -> Unit)? = null
    private var initEnded =
        false // if true allows the view to be updated after setting an attribute programmatically
    private var rememberToRenderCode = false
    private var xAnimator: ObjectAnimator? = null
    private var yAnimator: ObjectAnimator? = null
    private var editable: Editable = "".toEditable()
        set(value) {
            field = value
            if (initEnded) renderCode()
            else rememberToRenderCode = true
        }

    fun setOnCodeChangedListener(listener: ((Pair<String, Boolean>) -> Unit)?) {
        this.onCodeChangedListener = listener
    }

    var text: CharSequence
        get() = this.editable
        set(value) {
            val cropped = if (value.length > maxLength) value.subSequence(0, maxLength)
            else value
            this.editable = cropped.toEditable()
            binding.editCodeReal.setText(cropped)
        }

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        removeAllViews()
        addView(binding.root)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CodeEditText, 0, 0)
        try {
            // codeMaskChar
            attributes.getString(R.styleable.CodeEditText_cet_codeMaskChar)?.also {
                codeMaskChar = it[0]
            }

            // codePlaceholder
            attributes.getString(R.styleable.CodeEditText_cet_codePlaceholder)?.also {
                codePlaceholder = it[0]
            }

            // inputType
            if (attributes.hasValue(R.styleable.CodeEditText_android_inputType))
                binding.editCodeReal.inputType =
                    attributes.getInt(R.styleable.CodeEditText_android_inputType, 0)

            // maskTheCode
            if (attributes.hasValue(R.styleable.CodeEditText_cet_maskTheCode)) maskTheCode =
                attributes.getBoolean(R.styleable.CodeEditText_cet_maskTheCode, false)

            // maxLength
            maxLength = attributes.getInt(R.styleable.CodeEditText_android_maxLength, maxLength)

            // scrollDurationInMillis
            scrollDurationInMillis = attributes.getInt(
                R.styleable.CodeEditText_cet_scrollDurationInMillis,
                scrollDurationInMillis
            )

            // text
            attributes.getString(R.styleable.CodeEditText_android_text)?.also { value ->
                val cropped = if (value.length > maxLength) value.subSequence(0, maxLength)
                else value
                this.editable = cropped.toEditable()
            }

        } finally {
            attributes.recycle()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initEnded = true

        if (!isInEditMode) binding.apply {
            llCodeWrapper.removeAllViews()
            for (i in 0 until maxLength) {
                View.inflate(
                    context,
                    R.layout.item_code_edit_text,
                    findViewById(R.id.llCodeWrapper)
                )
            }

            if (editable.isNotEmpty()) editCodeReal.text = editable
            editCodeReal.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
            editCodeReal.removeTextChangedListener(textChangedListener)
            editCodeReal.addTextChangedListener(textChangedListener)

            llCodeWrapper.setOnClickListener {
                editCodeReal.apply {
                    showKeyboard()
                    focusOnLastLetter()
                }
            }
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
            editable = s
        }
    }

    private fun renderCode() {
        binding.apply {
            for (i in 0 until llCodeWrapper.childCount) {
                val itemContainer = llCodeWrapper.getChildAt(i)

                itemContainer.findViewById<TextView>(R.id.tvCode).text =
                    if (editable.length > i)
                        (if (maskTheCode) codeMaskChar else editable[i]).toString()
                    else codePlaceholder.toString()

                if (i == editable.length - 1 && !itemContainer.isFullyVisibleInside(
                        hsvCodeWrapperScroller
                    )
                )
                    hsvCodeWrapperScroller.focusOnView(itemContainer)
            }
        }
        notifyCodeChanged()
    }

    private fun notifyCodeChanged(): Boolean = (editable.length == maxLength).apply {
        onCodeChangedListener?.invoke(Pair(editable.toString(), this))
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

    private fun View.focusOnView(childView: View) = post {
        var top = childView.top
        var left = childView.left
        var parent = (childView.parent as View)
        while (parent != this) {
            top += parent.top
            left += parent.left
            parent = parent.parent as View
        }

        val scrollX = left - this.width / 2 + childView.width / 2
        xAnimator?.cancel()
        xAnimator = ObjectAnimator.ofInt(this, "scrollX", scrollX).apply {
            interpolator = DecelerateInterpolator()
            duration = scrollDurationInMillis.toLong()
            start()
        }

        val scrollY = top - this.height / 2 + childView.height / 2
        yAnimator?.cancel()
        yAnimator = ObjectAnimator.ofInt(this, "scrollY", scrollY).apply {
            interpolator = DecelerateInterpolator()
            duration = scrollDurationInMillis.toLong()
            start()
        }
    }

    private fun View.showKeyboard() {
        requestFocus()
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.also {
            it.showSoftInput(this, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
    }

    private fun EditText.focusOnLastLetter() = setSelection(text.length)

    private fun CharSequence.toEditable(): Editable =
        Editable.Factory.getInstance().newEditable(this)

}
