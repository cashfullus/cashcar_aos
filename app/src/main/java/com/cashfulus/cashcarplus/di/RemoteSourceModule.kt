package com.cashfulus.cashcarplus.di

import com.cashfulus.cashcarplus.data.remote.*
import org.koin.dsl.module

val remoteSourceModule = module {
    single<RemoteMainSource> { RemoteMainSourceImpl(service = get()) }
    single<RemoteUserSource> { RemoteUserSourceImpl(service = get()) }
    single<RemoteInquirySource> { RemoteInquirySourceImpl(service = get()) }
    single<RemoteNoticeSource> { RemoteNoticeSourceImpl(service = get()) }
    single<RemoteCarSource> { RemoteCarSourceImpl(service = get()) }
    single<RemoteMissionSource> { RemoteMissionSourceImpl(service = get()) }
    single<RemoteFaqSource> { RemoteFaqSourceImpl(service = get()) }
    single<RemoteAlarmSource> { RemoteAlarmSourceImpl(service = get()) }
    single<RemoteVersionSource> { RemoteVersionSourceImpl(service = get()) }
    single<RemoteCashcarTipSource> { RemoteCashcarTipSourceImpl(service = get()) }
    single<RemoteDonationSource> { RemoteDonationSourceImpl(service = get()) }
    single<RemotePointSource> { RemotePointSourceImpl(service = get()) }
}