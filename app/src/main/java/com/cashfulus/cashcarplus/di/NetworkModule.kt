package com.cashfulus.cashcarplus.di

import com.cashfulus.cashcarplus.data.service.Api
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


private const val CONNECT_TIMEOUT = 50L
private const val WRITE_TIMEOUT = 50L
private const val READ_TIMEOUT = 50L
private const val BASE_URL = "https://app.api.service.cashcarplus.com:50193/"

val networkModule = module {

    single {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
            connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            retryOnConnectionFailure(true)
        }.build()
    }

    single {
        Retrofit.Builder().client(get()).baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build() //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    }


    /*single {
        class AppInterceptor : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
                try {
                    val newRequest = request().newBuilder().build()
                    proceed(newRequest)
                } catch (exception: SocketTimeoutException) {
                    exception.printStackTrace()
                }

                val newRequest = request().newBuilder().build()

                proceed(newRequest)
            }
        }

        fun provideOkHttpClient(interceptor: AppInterceptor): OkHttpClient = OkHttpClient.Builder().run {
            addInterceptor(interceptor)
            build()
        }

        Retrofit.Builder().client(provideOkHttpClient(AppInterceptor())).baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build() //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    }*/

    single { get<Retrofit>().create(Api::class.java) }
}

interface OnConnectionTimeoutListener {
    fun onConnectionTimeout()
}