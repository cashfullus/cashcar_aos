package com.cashfulus.cashcarplus.util

import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.MutableLiveData
import com.cashfulus.cashcarplus.model.SingleLiveEvent
import com.cashfulus.cashcarplus.view.UpgradedEdittext
import java.util.regex.Pattern

/** 정규식 모음 */
// 이름 : 2글자 이상, 20자 이하
val nameRE = "^[가-힣a-zA-Z\\s]{2,20}\$"

// 닉네임 : 2글자 이상, 20자 이하
val nicknameRE = "^(?=.{2,20}\$).*"

// 비밀번호 : 영문+숫자 8자~
val passwordRE = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"
val passwordRE2 = "^(?=.*[\$@\$!%*#?&])[A-Za-z\\d\$@\$!%*#?&]{8,}\$"

// 전화번호 길이 : 10자~11자
val phoneLengthRE = "^(?=.{10,11}\$).*"

// 년도 : 2018
val yearRE = "^[1-2][0-9][0-9][0-9]\$"
// 생일 : 19860413
val birthRE = "^[1-2][0-9][0-9][0-9][0-1][0-9][0-3][0-9]\$"

// 차량 번호 : 123가4567
val carNumberRE1 = "^[0-9][0-9][0-9][가-힣][0-9][0-9][0-9][0-9]\$"
// 차량 번호 : 12가4567
val carNumberRE2 = "^[0-9][0-9][가-힣][0-9][0-9][0-9][0-9]\$"

// 모델명 : 한글/영어/숫자/공백 합쳐서 2글자 이상 30자 이하
val modelRE = "^[가-힣a-zA-Z0-9\\d-/&,\\s]{2,30}\$"

// 브랜드 : 한글/영어/숫자 합쳐서 2글자 이상 20자 이하
val brandRE = "^[가-힣a-zA-Z0-9]{2,20}\$"

// 소유자 : 한글/영어/숫자/공백 합쳐서 2글자 이상 30자 이하
val ownerRE = "^[가-힣a-zA-Z0-9\\d/&,\\s]{2,30}\$"

// 주행거리 : 9999 이하 자연수
val gaugeRE = "^[1-9]\$|^[1-9][0-9]\$|^[1-9][0-9][0-9]\$|^[1-9][0-9][0-9][0-9]\$|^[1-9][0-9][0-9][0-9][0-9]\$" +
        "|^[1-9][0-9][0-9][0-9][0-9][0-9]\$|^[1-9][0-9][0-9][0-9][0-9][0-9][0-9]\$|^[1-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]\$"

// 계좌번호 : 숫자 6~20자리
val accountRE = "^[0-9]{6,20}\$"

fun UpgradedEdittext.validate(validator: ((String) -> Boolean)?, maxLength: Int?, errMsg: String?, sucMsg: String, errLengthMsg: String?) {
    this.getEditText().addTextChangedListener {
        if(validator != null && !validator(it.toString()))
            setError(errMsg!!)
        else if(maxLength != null && it.toString().length > maxLength)
            setError(errLengthMsg!!)
        else
            setSuccess(sucMsg)
    }

    if(validator != null && !validator(this.getEditText().text.toString()))
        setError(errMsg!!)
    else if(maxLength != null && this.getEditText().text.toString().length > maxLength)
        setError(errLengthMsg!!)
    else
        setSuccess(sucMsg)
}

fun UpgradedEdittext.validateWithoutMsg(validator: ((String) -> Boolean)?) {
    this.getEditText().addTextChangedListener {
        if(validator != null && !validator(it.toString()))
            setErrorWithoutMsg()
        else
            setSuccessWithoutMsg()
    }

    if(validator != null && !validator(this.getEditText().text.toString()))
        setErrorWithoutMsg()
    else
        setSuccessWithoutMsg()
}


fun String.isValidName(): Boolean = Pattern.compile(nameRE).matcher(this).matches()
fun String.isValidNickname(): Boolean = Pattern.compile(nicknameRE).matcher(this).matches()
fun String.isValidEmail(): Boolean = this.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
fun String.isValidPhone(): Boolean = this.isNotEmpty() && Patterns.PHONE.matcher(this).matches() && Pattern.compile(phoneLengthRE).matcher(this.replace("-","")).matches()
fun String.isValidBirth(): Boolean {
    return if(this.isNotEmpty() && Pattern.compile(birthRE).matcher(this).matches()) { // 1단계 조건 통과
        this.subSequence(4,6).toString().toInt() <= 12 && this.subSequence(6,8).toString().toInt() <= 31
    } else {
        false
    }
}
fun String.isValidPassword(): Boolean = this.isNotEmpty() && (Pattern.compile(passwordRE).matcher(this).matches() || Pattern.compile(passwordRE2).matcher(this).matches())

fun String.isValidBrand(): Boolean = this.isNotEmpty() && Pattern.compile(brandRE).matcher(this).matches()
fun String.isValidModel(): Boolean = this.isNotEmpty() && Pattern.compile(modelRE).matcher(this).matches()
fun String.isValidYear(): Boolean = this.isNotEmpty() && Pattern.compile(yearRE).matcher(this).matches()
fun String.isValidCarNumber(): Boolean = this.isNotEmpty() && (Pattern.compile(carNumberRE1).matcher(this).matches() || Pattern.compile(carNumberRE2).matcher(this).matches())
fun String.isValidOwner(): Boolean = this.isNotEmpty() && Pattern.compile(ownerRE).matcher(this).matches()

fun String.isValidGauge(): Boolean = this.isNotEmpty() && Pattern.compile(gaugeRE).matcher(this).matches()
fun String.isValidAccount(): Boolean = this.isNotEmpty() && Pattern.compile(accountRE).matcher(this).matches()