package com.cashfulus.cashcarplus.ui.adinfo

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.cashfulus.cashcarplus.R
import kotlinx.android.synthetic.main.activity_address.*


class AddressActivity : AppCompatActivity() {

    private var handler: Handler? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        initWebView()

        handler = Handler()
    }

    fun initWebView() {
        val webView = findViewById<WebView>(R.id.wvAddress)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.setSupportMultipleWindows(true)
        webView.addJavascriptInterface(KaKaoJavaScriptInterface(), "CashCarApp")
        webView.webChromeClient = MyWebChromeClient()

        webView.loadUrl("https://app.api.service.cashcarplus.com:50193/kakao/postcode/and")
    }

    inner class KaKaoJavaScriptInterface {
        @JavascriptInterface
        fun setAddress(arg1: String?, arg2: String?, arg3: String?) {
            handler!!.post {
                Intent().apply {
                    Log.d("CashcarPlus", String.format("%s %s", arg2, arg3))
                    putExtra("address", String.format("%s %s", arg2, arg3))
                    setResult(RESULT_OK, this)
                }
                initWebView() // WebView를 초기화 하지않으면 재사용할 수 없음
                finish()
            }
        }
    }

    inner class MyWebChromeClient(): WebChromeClient() {
        override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
            val newWebView = WebView(this@AddressActivity)
            newWebView.settings.javaScriptEnabled = true

            val dialog = Dialog(this@AddressActivity)
            dialog.setContentView(newWebView)

            dialog.window!!.attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.attributes.height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.show()

            newWebView.webChromeClient = object: WebChromeClient(){
                override fun onCloseWindow(window: WebView?) {
                    dialog.dismiss()
                }
            }

            val result = resultMsg!!.obj as WebView.WebViewTransport
            result.webView = newWebView
            resultMsg.sendToTarget()
            return true
        }
    }

    override fun onBackPressed() {
        Intent().apply {
            setResult(RESULT_CANCELED, this)
        }
        finish()
    }
}