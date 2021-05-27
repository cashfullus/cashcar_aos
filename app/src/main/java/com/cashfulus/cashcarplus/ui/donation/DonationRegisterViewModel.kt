package com.cashfulus.cashcarplus.ui.donation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cashfulus.cashcarplus.base.BaseViewModel
import com.cashfulus.cashcarplus.data.repository.DonationRepository
import com.cashfulus.cashcarplus.model.DonationListResponseData
import com.cashfulus.cashcarplus.model.ErrorResponse
import com.cashfulus.cashcarplus.model.SingleLiveEvent

class DonationRegisterViewModel(private val repository: DonationRepository): BaseViewModel() {
    val response = MutableLiveData<DonationListResponseData>()
    val error = SingleLiveEvent<ErrorResponse>()


}