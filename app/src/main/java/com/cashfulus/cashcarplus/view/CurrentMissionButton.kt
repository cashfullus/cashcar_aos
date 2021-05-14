package com.cashfulus.cashcarplus.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import com.google.android.material.button.MaterialButton
import com.cashfulus.cashcarplus.R

/** 미션이 없는 상태 */
const val CURRENT_MISSION_NO_CAR = 0
const val CURRENT_MISSION_NONE = 1
/** 미션 인증 버튼의 상태값. */
const val CURRENT_CONSIDERATION_ENABLE_CANCEL = 2
const val CURRENT_CONSIDERATION_DISABLE_CANCEL = 3
/** 필수 미션 상태 */
const val CURRENT_MAIN_MISSION_START = 4
const val CURRENT_MAIN_MISSION_VERIFY = 5
const val CURRENT_MAIN_MISSION_RESEND = 6
const val CURRENT_ALL_MISSION_EMPTY = 133
/** 추가 미션 상태 */
const val CURRENT_SUB_MISSION_START = 7
const val CURRENT_SUB_MISSION_VERIFY = 8
const val CURRENT_SUB_MISSION_RESEND = 9

/** 미션 인증 버튼 (Figma에서 bt/mission/Or, bt/mission/Gr을 합친 것) */
class CurrentMissionButton: MaterialButton {
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
        this.typeface = ResourcesCompat.getFont(context, R.font.notosanskr_bold)
        this.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.0f)
        this.letterSpacing = 0f
        this.setPadding(0)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CurrentMissionButton) as TypedArray
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet, defStyle: Int) {
        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CurrentMissionButton, defStyle, 0) as TypedArray
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        // 버튼의 현재 상태
        val state = typedArray.getInt(R.styleable.CurrentMissionButton_curState, CURRENT_MAIN_MISSION_START)

        when(state) {
            CURRENT_MAIN_MISSION_START -> {
                this.background = ContextCompat.getDrawable(context, R.drawable.button_multiple_enable)
                this.typeface = ResourcesCompat.getFont(context, R.font.notosanskr_bold)
                this.setTextColor(context.getColor(R.color.grayscale_wt))
                this.text = "1차 미션 인증"
            }
            CURRENT_MAIN_MISSION_VERIFY -> {
                this.typeface = ResourcesCompat.getFont(context, R.font.notosanskr_medium)
                this.background = ContextCompat.getDrawable(context, R.drawable.button_multiple_disable)
                this.setTextColor(context.getColor(R.color.grayscale_400))
                this.text = "미션 검토 중입니다 :)"
            }
            CURRENT_ALL_MISSION_EMPTY -> {
                this.typeface = ResourcesCompat.getFont(context, R.font.notosanskr_medium)
                this.background = ContextCompat.getDrawable(context, R.drawable.button_multiple_disable)
                this.setTextColor(context.getColor(R.color.grayscale_400))
                this.text = "진행 가능한 미션이 없습니다."
            }
            CURRENT_MAIN_MISSION_RESEND -> {
                this.typeface = ResourcesCompat.getFont(context, R.font.notosanskr_bold)
                this.background = ContextCompat.getDrawable(context, R.drawable.button_multiple_enable)
                this.setTextColor(context.getColor(R.color.grayscale_wt))
                this.text = "재인증하기"
            }
            CURRENT_SUB_MISSION_START -> {
                this.typeface = ResourcesCompat.getFont(context, R.font.notosanskr_bold)
                this.background = ContextCompat.getDrawable(context, R.drawable.button_multiple_additional)
                this.setTextColor(context.getColor(R.color.grayscale_wt))
                this.text = "추가 미션 인증"
            }
            CURRENT_SUB_MISSION_VERIFY -> {
                this.typeface = ResourcesCompat.getFont(context, R.font.notosanskr_medium)
                this.background = ContextCompat.getDrawable(context, R.drawable.button_multiple_disable)
                this.setTextColor(context.getColor(R.color.grayscale_400))
                this.text = "미션 검토 중입니다 :)"
            }
            CURRENT_SUB_MISSION_RESEND -> {
                this.typeface = ResourcesCompat.getFont(context, R.font.notosanskr_bold)
                this.background = ContextCompat.getDrawable(context, R.drawable.button_multiple_additional)
                this.setTextColor(context.getColor(R.color.grayscale_wt))
                this.text = "재인증하기"
            }
        }

        typedArray.recycle()
    }

    fun setState(state: Int, text: String) {
        when(state) {
            CURRENT_MAIN_MISSION_START -> {
                this.typeface = ResourcesCompat.getFont(context, R.font.notosanskr_bold)
                this.background = ContextCompat.getDrawable(context, R.drawable.button_multiple_enable)
                this.setTextColor(context.getColor(R.color.grayscale_wt))
                this.text = text
            }
            CURRENT_MAIN_MISSION_VERIFY -> {
                this.typeface = ResourcesCompat.getFont(context, R.font.notosanskr_medium)
                this.background = ContextCompat.getDrawable(context, R.drawable.button_multiple_disable)
                this.setTextColor(context.getColor(R.color.grayscale_400))
                this.text = "미션 검토 중입니다 :)"
            }
            CURRENT_ALL_MISSION_EMPTY -> {
                this.typeface = ResourcesCompat.getFont(context, R.font.notosanskr_medium)
                this.background = ContextCompat.getDrawable(context, R.drawable.button_multiple_disable)
                this.setTextColor(context.getColor(R.color.grayscale_400))
                this.text = "진행 가능한 미션이 없습니다."
            }
            CURRENT_MAIN_MISSION_RESEND -> {
                this.typeface = ResourcesCompat.getFont(context, R.font.notosanskr_bold)
                this.background = ContextCompat.getDrawable(context, R.drawable.button_multiple_enable)
                this.setTextColor(context.getColor(R.color.grayscale_wt))
                this.text = "재인증하기"
            }
            CURRENT_SUB_MISSION_START -> {
                this.typeface = ResourcesCompat.getFont(context, R.font.notosanskr_bold)
                this.background = ContextCompat.getDrawable(context, R.drawable.button_multiple_additional)
                this.setTextColor(context.getColor(R.color.grayscale_wt))
                this.text = text
            }
            CURRENT_SUB_MISSION_VERIFY -> {
                this.typeface = ResourcesCompat.getFont(context, R.font.notosanskr_medium)
                this.background = ContextCompat.getDrawable(context, R.drawable.button_multiple_disable)
                this.setTextColor(context.getColor(R.color.grayscale_400))
                this.text = "미션 검토 중입니다 :)"
            }
            CURRENT_SUB_MISSION_RESEND -> {
                this.typeface = ResourcesCompat.getFont(context, R.font.notosanskr_bold)
                this.background = ContextCompat.getDrawable(context, R.drawable.button_multiple_additional)
                this.setTextColor(context.getColor(R.color.grayscale_wt))
                this.text = "재인증하기"
            }
        }
    }
}