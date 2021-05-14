package com.cashfulus.cashcarplus.ui.clause

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cashfulus.cashcarplus.R
import kotlinx.android.synthetic.main.activity_clause_contents.*

class ClauseContentsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clause_contents)

        toolbarClauseContents.setTitle(intent.getStringExtra("title")!!)
        wvClauseContents.settings.javaScriptEnabled = true
        wvClauseContents.loadUrl(intent.getStringExtra("url")!!)

        toolbarClauseContents.setLeftOnClick {
            finish()
        }
    }
}