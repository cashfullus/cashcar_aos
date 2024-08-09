package com.cashfulus.cashcarplus.ui.clause

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.view.UpgradedToolbar

class ClauseContentsActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clause_contents)

        var toolbarClauseContents = findViewById<UpgradedToolbar>(R.id.toolbarClauseContents)
        var wvClauseContents = findViewById<WebView>(R.id.toolbarClauseContents)

        toolbarClauseContents.setTitle(intent.getStringExtra("title")!!)
        wvClauseContents.settings.javaScriptEnabled = true
        wvClauseContents.loadUrl(intent.getStringExtra("url")!!)

        toolbarClauseContents.setLeftOnClick {
            finish()
        }
    }
}