package com.cashfulus.cashcarplus.view

import android.R.attr.maxLength
import android.content.Context
import android.content.res.TypedArray
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.*
import com.cashfulus.cashcarplus.R
import kotlinx.android.synthetic.main.widget_upgraded_edittext.view.*


class UpgradedEdittext : ConstraintLayout {
    var hasError: Boolean = false

    constructor(context: Context): super(context) {
        initLayout()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        initLayout()
        getAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(
        context,
        attrs,
        defStyle
    ) {
        initLayout()
        getAttrs(attrs, defStyle)
    }


    private fun initLayout() {
        LayoutInflater.from(context).inflate(R.layout.widget_upgraded_edittext, this, true)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.UET) as TypedArray
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet, defStyle: Int) {
        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.UET, defStyle, 0) as TypedArray
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        // 에러메세지 & 성공 메세지
        val strError = typedArray.getString(R.styleable.UET_errorMessage)
        val strSuccess = typedArray.getString(R.styleable.UET_successMessage)
        if((strError == null && strSuccess == null) || !edittext.isEnabled) {
            textview_message.visibility = View.GONE
            textview_message.setText("")
            hasError = false
        } else if(strSuccess != null) {
            textview_message.visibility = View.VISIBLE
            textview_message.setTextColor(context.getColor(R.color.system_success))
            textview_message.setText(strSuccess)
            edittext.background = context.getDrawable(R.drawable.input_form_success)
            hasError = false
        } else {
            textview_message.visibility = View.VISIBLE
            textview_message.setTextColor(context.getColor(R.color.system_no))
            textview_message.setText(strError)
            edittext.background = context.getDrawable(R.drawable.input_form_error)
            hasError = true
        }

        // 힌트
        val strHint = typedArray.getString(R.styleable.UET_hint)
        if (strHint != null) {
            edittext.hint = strHint
        } else {
            edittext.hint = ""
        }

        // isEnabled
        val isEnabled = typedArray.getBoolean(R.styleable.UET_enabled, true)
        edittext.isEnabled = isEnabled
        if(!isEnabled)
            textview_message.visibility = View.GONE

        // inputType
        val inputType = typedArray.getInt(R.styleable.UET_inputType, -1)
        if(inputType != -1)
            edittext.inputType = inputType

        // 최대 글자 수
        val length = typedArray.getInt(R.styleable.UET_length, 200)
        val fArray = arrayOfNulls<InputFilter>(1)
        fArray[0] = LengthFilter(length)
        edittext.filters = fArray

        // 최대 글자 수
        val maxLines = typedArray.getInt(R.styleable.UET_maxLines, 1)
        edittext.maxLines = maxLines

        typedArray.recycle()
    }

    fun setFilters(filterArrays: Array<InputFilter>) {
        edittext.filters = filterArrays
    }

    fun setError(strError: String) {
        if(edittext.isEnabled) {
            textview_message.visibility = View.VISIBLE
            textview_message.setTextColor(context.getColor(R.color.system_no))
            textview_message.setText(strError)
            edittext.background = context.getDrawable(R.drawable.input_form_error)

            hasError = true
        }
    }
    fun setErrorWithoutMsg() {
        if(edittext.isEnabled) {
            edittext.background = context.getDrawable(R.drawable.input_form_error)
            hasError = true
        }
    }

    fun setSuccess(strSuccess: String) {
        if(edittext.isEnabled) {
            textview_message.visibility = View.VISIBLE
            textview_message.setTextColor(context.getColor(R.color.system_success))
            textview_message.setText(strSuccess)
            edittext.background = context.getDrawable(R.drawable.input_form_success)

            hasError = false
        }
    }
    fun setSuccessWithoutMsg() {
        if(edittext.isEnabled) {
            edittext.background = context.getDrawable(R.drawable.input_form_success)
            hasError = false
        }
    }

    fun setNoError() {
        textview_message.visibility = View.GONE
        textview_message.setText("")
        edittext.background = context.getDrawable(R.drawable.input_form_selector)

        hasError = false
    }

    fun setFocus() {
        edittext.requestFocus()
    }

    fun getEditText(): EditText {
        return edittext
    }

    override fun setEnabled(flag: Boolean) {
        edittext.isEnabled = flag
        edittext.isFocusable = flag
        edittext.isClickable = flag
        edittext.isFocusableInTouchMode = flag
        if(!flag)
            textview_message.visibility = View.GONE
    }

    companion object {
        @JvmStatic
        @BindingAdapter("text")
        fun setUpgradedEditTextText(view: UpgradedEdittext, text: String?) {
            val old = view.getEditText().text.toString()
            if(old != text)
                view.getEditText().setText(text)
        }

        @JvmStatic
        @BindingAdapter("textAttrChanged")
        fun setUpgradedEditTextInverseBindingListener(
            view: UpgradedEdittext,
            listener: InverseBindingListener?
        ) {
            val watcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    listener?.onChange()
                }
            }

            view.getEditText().addTextChangedListener(watcher)
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "text", event = "textAttrChanged")
        fun getUpgradedEditTextText(view: UpgradedEdittext): String {
            return view.getEditText().text.toString()
        }
    }
}