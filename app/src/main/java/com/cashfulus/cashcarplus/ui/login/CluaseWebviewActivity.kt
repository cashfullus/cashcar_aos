package com.cashfulus.cashcarplus.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.view.UpgradedToolbar

class CluaseWebviewActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cluase_webview)

        var toolbarClause2 = findViewById<UpgradedToolbar>(R.id.toolbarClause2)
        var wvCluase2 = findViewById<WebView>(R.id.wvCluase2)

        toolbarClause2.setTitle(intent.getStringExtra("title")!!)
        wvCluase2.settings.javaScriptEnabled = true
        wvCluase2.loadUrl(intent.getStringExtra("url")!!)

        toolbarClause2.setLeftOnClick {
            val cIntent = Intent()
            cIntent.putExtra("number", intent.getIntExtra("number", 0))
            setResult(CLAUSE_CANCELED, cIntent)
            finish()
        }

        // Back button handling
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val cIntent = Intent().apply {
                    putExtra("number", intent.getIntExtra("number", 0))
                }
                setResult(CLAUSE_CANCELED, cIntent)
                finish()
            }
        })

    }

}