package com.cashfulus.cashcarplus.ui.clause

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.view.UpgradedToolbar

class ClauseListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clause_list)

        var toolbarClauseList = findViewById<UpgradedToolbar>(R.id.toolbarClauseList)
        var llClauseList1 = findViewById<LinearLayout>(R.id.llClauseList1)
        var llClauseList2 = findViewById<LinearLayout>(R.id.llClauseList2)
        var llClauseList3 = findViewById<LinearLayout>(R.id.llClauseList3)

        toolbarClauseList.setLeftOnClick {
            finish()
        }

        llClauseList1.setOnClickListener {
            val intent = Intent(this@ClauseListActivity, ClauseContentsActivity::class.java)
            intent.putExtra("url", getString(R.string.url_clause_service))
            intent.putExtra("title", "이용 약관")
            startActivity(intent)
        }

        llClauseList2.setOnClickListener {
            val intent = Intent(this@ClauseListActivity, ClauseContentsActivity::class.java)
            intent.putExtra("url", getString(R.string.url_privacy_policy))
            intent.putExtra("title", "개인정보 처리 방침")
            startActivity(intent)
        }

        llClauseList3.setOnClickListener {
            val intent = Intent(this@ClauseListActivity, ClauseContentsActivity::class.java)
            intent.putExtra("url", getString(R.string.url_location_based_service))
            intent.putExtra("title", "위치기반서비스 이용약관")
            startActivity(intent)
        }
    }
}