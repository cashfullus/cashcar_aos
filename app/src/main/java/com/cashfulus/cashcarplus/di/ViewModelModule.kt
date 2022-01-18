package com.cashfulus.cashcarplus.di

import com.cashfulus.cashcarplus.ui.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.cashfulus.cashcarplus.ui.adinfo.AdInfoViewModel
import com.cashfulus.cashcarplus.ui.adinfo.AdRegisterViewModel
import com.cashfulus.cashcarplus.ui.mission.MissionViewModel
import com.cashfulus.cashcarplus.ui.alarm.AlarmViewModel
import com.cashfulus.cashcarplus.ui.car.AddCarViewModel
import com.cashfulus.cashcarplus.ui.car.CarInfoViewModel
import com.cashfulus.cashcarplus.ui.car.MyCarViewModel
import com.cashfulus.cashcarplus.ui.donation.DonationListViewModel
import com.cashfulus.cashcarplus.ui.donation.DonationRegisterViewModel
import com.cashfulus.cashcarplus.ui.faq.FaqViewModel
import com.cashfulus.cashcarplus.ui.inquiry.InquiryViewModel
import com.cashfulus.cashcarplus.ui.login.EmailLoginViewModel
import com.cashfulus.cashcarplus.ui.login.LoginViewModel
import com.cashfulus.cashcarplus.ui.login.RegisterBasicViewModel
import com.cashfulus.cashcarplus.ui.login.RegisterViewModel
import com.cashfulus.cashcarplus.ui.mission.MissionCertViewModel
import com.cashfulus.cashcarplus.ui.myactivities.MyActivitiesViewModel
import com.cashfulus.cashcarplus.ui.notice.NoticeListViewModel
import com.cashfulus.cashcarplus.ui.point.PointViewModel
import com.cashfulus.cashcarplus.ui.user.UserAddressViewModel
import com.cashfulus.cashcarplus.ui.user.UserInfoViewModel
import com.cashfulus.cashcarplus.ui.withdraw.WithdrawViewModel

val viewModelModule = module {
    viewModel { SplashViewModel(repository = get(), versionRepo = get()) }
    viewModel { HomeViewModel(missionRepository = get()) }
    viewModel { InquiryViewModel(repository = get()) }
    viewModel { NoticeListViewModel(repository = get()) }
    viewModel { AdListViewModel(missionRepository = get()) }
    viewModel { LoginViewModel(repository = get()) }
    viewModel { MyViewModel(repository = get()) }
    viewModel { UserInfoViewModel(repository = get()) }
    viewModel { MyCarViewModel(repository = get()) }
    viewModel { CarInfoViewModel(repository = get()) }
    viewModel { AddCarViewModel(repository = get()) }
    viewModel { AdInfoViewModel(missionRepository = get()) }
    viewModel { AdRegisterViewModel(missionRepository = get()) }
    viewModel { AlarmViewModel(repository = get()) }
    viewModel { MissionViewModel(missionRepository = get()) }
    viewModel { MissionCertViewModel(repository = get()) }
    viewModel { UserAddressViewModel(repository = get()) }
    viewModel { EmailLoginViewModel(repository = get()) }
    viewModel { RegisterBasicViewModel(repository = get()) }
    viewModel { RegisterViewModel(repository = get()) }
    viewModel { FaqViewModel(repository = get()) }
    viewModel { CashcartipViewModel(repository = get()) }
    viewModel { com.cashfulus.cashcarplus.ui.cashcartip.CashcartipViewModel(repository = get()) }
    viewModel { DrivingViewModel() }
    viewModel { PointViewModel(repository = get()) }
    viewModel { MyActivitiesViewModel() }
    viewModel { DonationListViewModel(repository = get()) }
    viewModel { DonationRegisterViewModel(repository = get()) }
    viewModel { WithdrawViewModel(repository = get()) }
    // viewModel { (activity: Activity) -> SignupViewModel(repository = get(), activity = activity) }
    //viewModel { ChannelInfoViewModel(channelId = "dfd", repository = get()) }
    //viewModel { SocketViewModel() }
}