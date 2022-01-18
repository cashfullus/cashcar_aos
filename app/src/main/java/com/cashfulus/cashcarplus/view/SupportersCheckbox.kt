package com.cashfulus.cashcarplus.view

import android.content.Context
import android.content.res.TypedArray
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.checkbox.MaterialCheckBox
import com.cashfulus.cashcarplus.R
import kotlinx.android.synthetic.main.widget_supporters_checkbox.view.*

/** 광고 서포터즈 차량 체크박스의 상태. */
const val ONLY_ONE_CAR = 0
const val IS_SUPPORTERS_CAR = 1
const val NOT_SUPPORTERS_CAR = 2
const val ONLY_ONE_CAR_NOT_CHECKED = 3

class SupportersCheckbox : LinearLayout {

    var state = -1

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
        if (attrs != null) {
            getAttrs(attrs)
        }
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
        if (attrs != null) {
            getAttrs(attrs, defStyleAttr)
        }
    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.widget_supporters_checkbox, this, true)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SupportersCheckbox) as TypedArray
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet, defStyle: Int) {
        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SupportersCheckbox, defStyle, 0) as TypedArray
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        // 버튼의 현재 상태
        state = typedArray.getInt(R.styleable.SupportersCheckbox_checkState, ONLY_ONE_CAR)
        when(state) {
            ONLY_ONE_CAR -> {
                checkbox.background = context.getDrawable(R.drawable.ic_checkbox_deactive_selected)
                checkbox.buttonTintList = context.resources.getColorStateList(android.R.color.transparent)
                checkbox.isChecked = true
                textview_checkbox.setTextColor(context.getColor(R.color.grayscale_500))

                checkbox.isEnabled = false
                checkbox.isFocusable = false
                checkbox.isClickable = false
                textview_checkbox.isEnabled = false
                textview_checkbox.isFocusable = false
                textview_checkbox.isClickable = false
            }
            ONLY_ONE_CAR_NOT_CHECKED -> {
                checkbox.background = context.getDrawable(R.drawable.ic_checkbox_deactive_unselected)
                checkbox.buttonTintList = context.resources.getColorStateList(android.R.color.transparent)
                checkbox.isChecked = false
                textview_checkbox.setTextColor(context.getColor(R.color.grayscale_500))

                checkbox.isEnabled = false
                checkbox.isFocusable = false
                checkbox.isClickable = false
                textview_checkbox.isEnabled = false
                textview_checkbox.isFocusable = false
                textview_checkbox.isClickable = false
            }
            IS_SUPPORTERS_CAR -> {
                checkbox.background = context.getDrawable(R.drawable.selector_checkbox)
                checkbox.buttonTintList = context.resources.getColorStateList(android.R.color.transparent)
                checkbox.isChecked = true
                textview_checkbox.setTextColor(context.getColor(R.color.grayscale_bk))

                checkbox.isEnabled = true
                checkbox.isFocusable = true
                checkbox.isClickable = true
                textview_checkbox.isEnabled = true
                textview_checkbox.isFocusable = true
                textview_checkbox.isClickable = true
            }
            NOT_SUPPORTERS_CAR -> {
                checkbox.background = context.getDrawable(R.drawable.selector_checkbox)
                checkbox.buttonTintList = context.resources.getColorStateList(android.R.color.transparent)
                checkbox.isChecked = false
                textview_checkbox.setTextColor(context.getColor(R.color.grayscale_bk))

                checkbox.isEnabled = true
                checkbox.isFocusable = true
                checkbox.isClickable = true
                textview_checkbox.isEnabled = true
                textview_checkbox.isFocusable = true
                textview_checkbox.isClickable = true
            }
        }

        typedArray.recycle()
    }

    fun isEnabled(status: Boolean) {
        checkbox.isEnabled = status
        checkbox.isClickable = status
        checkbox.isFocusable = status
        textview_checkbox.isClickable = status
    }

    fun setCurrentState(state: Int) {
        when(state) {
            ONLY_ONE_CAR -> {
                checkbox.background = context.getDrawable(R.drawable.ic_checkbox_deactive_selected)
                checkbox.buttonTintList = context.resources.getColorStateList(android.R.color.transparent)
                checkbox.isChecked = true
                textview_checkbox.setTextColor(context.getColor(R.color.grayscale_500))

                checkbox.isEnabled = false
                checkbox.isFocusable = false
                checkbox.isClickable = false
                textview_checkbox.isEnabled = false
                textview_checkbox.isFocusable = false
                textview_checkbox.isClickable = false
            }
            ONLY_ONE_CAR_NOT_CHECKED -> {
                checkbox.background = context.getDrawable(R.drawable.ic_checkbox_deactive_unselected)
                checkbox.buttonTintList = context.resources.getColorStateList(android.R.color.transparent)
                checkbox.isChecked = false
                textview_checkbox.setTextColor(context.getColor(R.color.grayscale_500))

                checkbox.isEnabled = false
                checkbox.isFocusable = false
                checkbox.isClickable = false
                textview_checkbox.isEnabled = false
                textview_checkbox.isFocusable = false
                textview_checkbox.isClickable = false
            }
            IS_SUPPORTERS_CAR -> {
                checkbox.background = context.getDrawable(R.drawable.selector_checkbox)
                checkbox.buttonTintList = context.resources.getColorStateList(android.R.color.transparent)
                checkbox.isChecked = true
                textview_checkbox.setTextColor(context.getColor(R.color.grayscale_bk))

                checkbox.isEnabled = true
                checkbox.isFocusable = true
                checkbox.isClickable = true
                textview_checkbox.isEnabled = true
                textview_checkbox.isFocusable = true
                textview_checkbox.isClickable = true
            }
            NOT_SUPPORTERS_CAR -> {
                checkbox.background = context.getDrawable(R.drawable.selector_checkbox)
                checkbox.buttonTintList = context.resources.getColorStateList(android.R.color.transparent)
                checkbox.isChecked = false
                textview_checkbox.setTextColor(context.getColor(R.color.grayscale_bk))

                checkbox.isEnabled = true
                checkbox.isFocusable = true
                checkbox.isClickable = true
                textview_checkbox.isEnabled = true
                textview_checkbox.isFocusable = true
                textview_checkbox.isClickable = true
            }
        }
    }

    fun getCheckbox(): CheckBox {
        return checkbox
    }

    companion object {
        @JvmStatic
        @BindingAdapter("isChecked")
        fun setCheckedSupportedCheckbox(view: SupportersCheckbox, checked: Boolean) {
            view.getCheckbox().isChecked = checked
        }

        @JvmStatic
        @BindingAdapter("isCheckedAttrChanged")
        fun setCheckedSupportedCheckboxInverseBindingListener(view: SupportersCheckbox, listener: InverseBindingListener?) {
            view.getCheckbox().setOnCheckedChangeListener { buttonView, isChecked ->
                //view.getCheckbox().isChecked = isChecked
                listener?.onChange()
            }
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "isChecked", event = "isCheckedAttrChanged")
        fun getCheckedSupportedCheckbox(view: SupportersCheckbox): Boolean {
            return view.getCheckbox().isChecked
        }
    }
}