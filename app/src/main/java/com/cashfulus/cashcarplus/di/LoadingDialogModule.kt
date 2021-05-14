package com.cashfulus.cashcarplus.di

import android.content.Context
import org.koin.dsl.module
import com.cashfulus.cashcarplus.ui.dialog.LoadingDialog

val loadingModule = module {
    single {(context: Context) ->
        LoadingDialog(context)
    }
}