package com.cashfulus.cashcarplus.base

import android.app.Application
import android.content.Context
import com.cashfulus.cashcarplus.R
import com.kakao.sdk.common.KakaoSdk
import com.cashfulus.cashcarplus.di.*
import com.kakao.ad.tracker.KakaoAdTracker
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {
    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Kakao AD SDK 초기화
        if (!KakaoAdTracker.isInitialized) {
            KakaoAdTracker.init(applicationContext, getString(R.string.kakao_ad_track_id))
        }

        // 앱 실행 시 Koin 설정, 의존성 주입.
        startKoin {
            // 안드로이드 로그 기능 사용
            // 참고 : https://stackoverflow.com/questions/63393507/java-lang-nosuchmethoderror-no-virtual-method-elapsednowd-in-class-lkotlin-ti
            androidLogger(Level.NONE)
            // Android Context 넘겨줌
            androidContext(this@App)
            // assets/koin.properties 파일에서 프로퍼티를 가져옴.
            androidFileProperties()

            // 모듈 목록 설정
            modules(networkModule, remoteSourceModule, repositoryModule, viewModelModule)
        }

//        KakaoSdk.init(this, "c4a351e2bdeb2dd3bf4e4822fc9aca19")
        KakaoSdk.init(this, "a30e091716f0380da05030eeb5eb324c")
    }

    fun context(): Context = instance
}