package com.cashfulus.cashcarplus.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import com.google.android.material.button.MaterialButton
import com.cashfulus.cashcarplus.R

/** 미션 인증 버튼의 상태값. */
const val MISSION_STATE_NEED_CERT = 0
const val MISSION_STATE_WAITING = 1
const val MISSION_STATE_CHECKING = 2
const val MISSION_STATE_SUCCESS = 3
const val MISSION_STATE_FAIL = 4
const val MISSION_STATE_NEED_RECERT = 5
const val ADDTIONAL_MISSION_STATE_NEED_CERT = 6
const val ADDTIONAL_MISSION_STATE_WAITING = 7
const val ADDTIONAL_MISSION_STATE_CHECKING = 8
const val ADDTIONAL_MISSION_STATE_SUCCESS = 9
const val ADDTIONAL_MISSION_STATE_FAIL = 10
const val ADDTIONAL_MISSION_STATE_NEED_RECERT = 11
const val DRIVING_STATE_NEED_CERT = 12
const val DRIVING_STATE_CHECKING = 13
const val DRIVING_STATE_SUCCESS = 14
const val DRIVING_STATE_NEED_RECERT = 15

/** 드라이빙 인증 버튼에서 사용되지 않는 상태값들 */
const val DRIVING_STATE_WAITING = 997
const val DRIVING_STATE_FAIL = 999

/** 미션 인증 버튼 (Figma에서 bt/mission/Or, bt/mission/Gr을 합친 것) */
class MissionButton: MaterialButton {
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
        this.typeface = ResourcesCompat.getFont(context, R.font.notosanskr_medium)
        this.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f)
        this.letterSpacing = 0f
        this.setPadding(0)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MissionButton) as TypedArray
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet, defStyle: Int) {
        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MissionButton, defStyle, 0) as TypedArray
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        // 버튼의 현재 상태
        val state = typedArray.getInt(R.styleable.MissionButton_state, MISSION_STATE_NEED_CERT)
        setState(state)

        typedArray.recycle()
    }

    fun setState(state: Int) {
        when(state) {
            MISSION_STATE_NEED_CERT -> {
                this.background = context.getDrawable(R.drawable.button_mission_need_cert)
                this.setTextColor(context.getColor(R.color.grayscale_wt))
                this.text = "인증하기"
            }
            MISSION_STATE_WAITING -> {
                this.background = null
                this.setTextColor(context.getColor(R.color.grayscale_400))
                this.text = "미션 대기"
            }
            MISSION_STATE_CHECKING -> {
                this.background = null
                this.setTextColor(context.getColor(R.color.brand_orange1))
                this.text = "검토중"
            }
            MISSION_STATE_SUCCESS -> {
                this.background = context.getDrawable(R.drawable.button_mission_success)
                this.setTextColor(context.getColor(R.color.brand_orange1))
                this.text = "미션 성공"
            }
            MISSION_STATE_FAIL -> {
                this.background = context.getDrawable(R.drawable.button_mission_fail)
                this.setTextColor(context.getColor(R.color.grayscale_wt))
                this.text = "미션 실패"
            }
            MISSION_STATE_NEED_RECERT -> {
                this.background = context.getDrawable(R.drawable.button_mission_need_cert)
                this.setTextColor(context.getColor(R.color.grayscale_wt))
                this.text = "재인증하기"
            }

            ADDTIONAL_MISSION_STATE_NEED_CERT -> {
                this.background = context.getDrawable(R.drawable.button_mission_additional_need_cert)
                this.setTextColor(context.getColor(R.color.grayscale_wt))
                this.text = "인증하기"
            }
            ADDTIONAL_MISSION_STATE_WAITING -> {
                this.background = null
                this.setTextColor(context.getColor(R.color.grayscale_400))
                this.text = "미션 대기"
            }
            ADDTIONAL_MISSION_STATE_CHECKING -> {
                this.background = null
                this.setTextColor(context.getColor(R.color.brand_green))
                this.text = "검토중"
            }
            ADDTIONAL_MISSION_STATE_SUCCESS -> {
                this.background = context.getDrawable(R.drawable.button_mission_additional_success)
                this.setTextColor(context.getColor(R.color.brand_green))
                this.text = "미션 성공"
            }
            ADDTIONAL_MISSION_STATE_FAIL -> {
                this.background = context.getDrawable(R.drawable.button_mission_fail)
                this.setTextColor(context.getColor(R.color.grayscale_wt))
                this.text = "미션 실패"
            }
            ADDTIONAL_MISSION_STATE_NEED_RECERT -> {
                this.background = context.getDrawable(R.drawable.button_mission_additional_need_cert)
                this.setTextColor(context.getColor(R.color.grayscale_wt))
                this.text = "재인증하기"
            }
            DRIVING_STATE_NEED_CERT -> {
                this.background = context.getDrawable(R.drawable.button_driving_need_cert)
                this.setTextColor(context.getColor(R.color.grayscale_wt))
                this.text = "인증하기"
            }
            DRIVING_STATE_CHECKING -> {
                this.background = null
                this.setTextColor(context.getColor(R.color.purple))
                this.text = "검토중"
            }
            DRIVING_STATE_SUCCESS -> {
                this.background = context.getDrawable(R.drawable.button_driving_success)
                this.setTextColor(context.getColor(R.color.purple))
                this.text = "미션 성공"
            }
            DRIVING_STATE_NEED_RECERT -> {
                this.background = context.getDrawable(R.drawable.button_driving_need_cert)
                this.setTextColor(context.getColor(R.color.grayscale_wt))
                this.text = "재인증하기"
            }
        }
    }
}