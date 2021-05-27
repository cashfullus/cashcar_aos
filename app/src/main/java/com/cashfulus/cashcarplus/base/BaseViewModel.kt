package com.cashfulus.cashcarplus.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

interface BaseViewModelInt {
    fun showLoadingDialog()
    fun hideLoadingDialog()
}

abstract class BaseViewModel: ViewModel(), BaseViewModelInt {
    val isDialogVisible = MutableLiveData<Boolean>(false)

    override fun showLoadingDialog() {
        isDialogVisible.postValue(true)
    }

    override fun hideLoadingDialog() {
        isDialogVisible.postValue(false)
    }
}