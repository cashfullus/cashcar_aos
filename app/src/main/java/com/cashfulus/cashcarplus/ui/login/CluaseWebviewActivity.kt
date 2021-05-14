package com.cashfulus.cashcarplus.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cashfulus.cashcarplus.R
import kotlinx.android.synthetic.main.activity_cluase_webview.*

class CluaseWebviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cluase_webview)

        toolbarClause2.setTitle(intent.getStringExtra("title")!!)
        wvCluase2.settings.javaScriptEnabled = true
        wvCluase2.loadUrl(intent.getStringExtra("url")!!)

        toolbarClause2.setLeftOnClick {
            val cIntent = Intent()
            cIntent.putExtra("number", intent.getIntExtra("number", 0))
            setResult(CLAUSE_CANCELED, cIntent)
            finish()
        }
    }

    override fun onBackPressed() {
        val cIntent = Intent()
        cIntent.putExtra("number", intent.getIntExtra("number", 0))
        setResult(CLAUSE_CANCELED, cIntent)
        finish()
    }
}