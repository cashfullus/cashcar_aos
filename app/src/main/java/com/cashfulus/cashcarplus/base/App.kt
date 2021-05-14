package com.cashfulus.cashcarplus.base

import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk
import com.cashfulus.cashcarplus.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // 앱 실행 시 Koin 설정, 의존성 주입.
        startKoin {
            // 안드로이드 로그 기능 사용
            androidLogger()
            // Android Context 넘겨줌
            androidContext(this@App)
            // assets/koin.properties 파일에서 프로퍼티를 가져옴.
            androidFileProperties()

            // 모듈 목록 설정
            modules(loadingModule, networkModule, remoteSourceModule, repositoryModule, viewModelModule)
        }

        KakaoSdk.init(this, "c4a351e2bdeb2dd3bf4e4822fc9aca19")
    }

    fun context(): Context = instance
}