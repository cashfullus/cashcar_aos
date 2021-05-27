package com.cashfulus.cashcarplus.ui.donation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cashfulus.cashcarplus.R
import kotlinx.android.synthetic.main.activity_donation_privacy.*

class DonationPrivacyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation_privacy)

        val html = """<!DOCTYPE html>
<html lang="ko">
  <head>
    <title>이용 약관</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,height=device-height,initial-scale=1.0"/>
  </head>
  <body>
   <div id = "layer" style = "display:block; position:absolute; overflow:hidden; z-index:1; -webkit-overflow-scrolling:touch;">
      <p style="word-break: break-all; font-size: 12px;">
        캐시카플러스는 기부 서비스의 원활한 진행을 위해 다음과 같은 정보를 각 기부 단체에 제공합니다.<br>
        <br>
        제공받는 자 : 각 기부 단체<br>
        제공 목적 : 기부금 영수증을 신청해주신 분에 한하여 기부 단체에 정보가 제공됨<br>
        제공하는 개인정보 : 이름, 전화 번호, 생년월일, 금액 <br>
        제공받는 자의 개인정보 보유기간 : 영수증 발급 시 폐기<br>
        <br>
        * 고객님께서는 개인정보 제공에 동의하지 않으실 수 있으며, 동의하지 않으신 경우 본 서비스 이용이 제한 될 수 있습니다.<br>
        <br>
        * 회사는 상기 정보와 목적 외에는 이용자의 정보를 제공하지 않으며, 서비스 이용을 위한 목적 외에는 고객정보를 활용하지 않습니다.<br>
        <br>
        시행일 : 이 약관은 2021년 05월 24일부터 시행합니다.<br>
        <br>
      </p>
   </div>
  </body>
</html>"""
        wvDonationPolicy.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)

        toolbarDonationPolicy.setLeftOnClick {
            finish()
        }
    }
}