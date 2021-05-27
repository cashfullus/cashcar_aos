package com.cashfulus.cashcarplus.ui.notice

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.base.BaseViewModel
import com.cashfulus.cashcarplus.data.repository.NoticeRepository
import com.cashfulus.cashcarplus.data.service.NO_INTERNET_ERROR_CODE
import com.cashfulus.cashcarplus.model.*
import com.cashfulus.cashcarplus.util.NetworkManager
import com.cashfulus.cashcarplus.util.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoticeListViewModel(private val repository: NoticeRepository): BaseViewModel() {
    val noticeList = MutableLiveData<NoticeDataList>()
    val error = SingleLiveEvent<ErrorResponse>()

    init {
        loadNoticeList()
    }

    fun loadNoticeList() {
        if(NetworkManager().checkNetworkState()) {
            CoroutineScope(Dispatchers.IO).launch {
                val apiRespose = repository.getNoticeList(UserManager.userId!!, UserManager.jwtToken!!)

                if (apiRespose.isSucceed)
                    noticeList.postValue(apiRespose.contents!!)
                else {
                    error.postValue(apiRespose.error!!)
                }
            }
        } else {
            error.postValue(makeErrorResponseFromStatusCode(NO_INTERNET_ERROR_CODE, ""))
        }
    }
}