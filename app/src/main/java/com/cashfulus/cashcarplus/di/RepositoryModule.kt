package com.cashfulus.cashcarplus.di

import android.app.Activity
import com.cashfulus.cashcarplus.data.repository.*
import org.koin.dsl.module

val repositoryModule = module {
    single<MainRepository> { MainRepositoryImpl(remoteMainSource = get()) }
    single<UserRepository> { UserRepositoryImpl(remoteUserSource = get()) }
    single<InquiryRepository> { InquiryRepositoryImpl(remoteInquirySource = get()) }
    single<NoticeRepository> { NoticeRepositoryImpl(remoteNoticeSource = get()) }
    single<MissionRepository> { MissionRepositoryImpl(remoteMissionSource = get()) }
    single<AdRepository> { AdRepositoryImpl(remoteMissionSource = get()) }
    single<CarRepository> { CarRepositoryImpl(remoteCarSource = get()) }
    single<AlarmRepository> { AlarmRepositoryImpl(remoteAlarmSource = get()) }
    single<FaqRepository> { FaqRepositoryImpl(remoteFaqSource = get()) }
    single<VersionRepository> { VersionRepositoryImpl(remoteVersionSource = get()) }
    single<CashcarTipRepository> { CashcarTipRepositoryImpl(remoteCashcarTipSource = get()) }
    single<DonationRepository> { DonationRepositoryImpl(remoteDonationSource = get()) }
    single<PointRepository> { PointRepositoryImpl(remotePointSource = get()) }

    /// Repository 내에서 Callback을 사용하는 경우.
    /* single<CertificationRepository> {
            (callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks, success: (m: FirebaseUser?) -> Unit,
                failed: (m: String) -> Unit, activity: Activity) ->
        CertificationRepositoryImpl(callbacks = callbacks, success = success, failed = failed, activity = activity)
    } */
}