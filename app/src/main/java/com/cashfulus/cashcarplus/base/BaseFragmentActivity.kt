package com.cashfulus.cashcarplus.base

import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity

open class BaseFragmentActivity : FragmentActivity() {
    protected inline fun <reified T : ViewDataBinding> binding(@LayoutRes resId: Int): Lazy<T> =
        lazy { DataBindingUtil.setContentView<T>(this, resId) }

    fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    fun showToastShort(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}