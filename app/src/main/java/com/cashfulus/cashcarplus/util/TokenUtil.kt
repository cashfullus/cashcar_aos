package com.cashfulus.cashcarplus.util

import android.content.SharedPreferences
import android.text.TextUtils
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.cashfulus.cashcarplus.base.App

/** accessToken, refreshToken을 저장한다. */
suspend fun saveToken(accessToken: String, refreshToken: String) {
    val sharedPreferences: SharedPreferences by lazy {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        EncryptedSharedPreferences.create(
            "ourstock_tokens",
            masterKeyAlias,
            App().context(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    }

    sharedPreferences
        .edit()
        .putString("access", accessToken)
        .putString("refresh", refreshToken)
        .apply()
}

/** 저장된 accessToken을 불러온다. */
fun getAccessToken(): String? {
    val sharedPreferences: SharedPreferences by lazy {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        EncryptedSharedPreferences.create(
            "ourstock_tokens",
            masterKeyAlias,
            App().context(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    }

    return if(TextUtils.isEmpty(sharedPreferences.getString("access", "")))
        null
    else
        sharedPreferences.getString("access", "")
}

/** 저장된 refreshToken을 불러온다. */
fun getRefreshToken(): String? {
    val sharedPreferences: SharedPreferences by lazy {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        EncryptedSharedPreferences.create(
            "ourstock_tokens",
            masterKeyAlias,
            App().context(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    }

    return if(TextUtils.isEmpty(sharedPreferences.getString("refresh", "")))
        null
    else
        sharedPreferences.getString("refresh", "")
}