package com.cashfulus.cashcarplus.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.ui.MainActivity
import com.cashfulus.cashcarplus.ui.SplashActivity
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


// (https://medium.com/harrythegreat/android-fcm-data%EC%99%80-notification-36a5285cfae5)
// 주제별 구독은 맨 첨에 처리 안 하면 인식되지 않음
// notification에 정보를 보내는 경우 notification이 포함된 코드를 쓰면 된다. 단, 그 경우 background에서 data 수신이 이루어지지 않는 문제가 있다.


/** 기능이 업데이트되면서, 특별한 추가 작업이 없어도 포어그라운드 상태에서 불러오는 게 가능한 걸로 보인다.*/
class CashcarFirebaseMessagingService: FirebaseMessagingService() {
    private val TAG = "CashcarAlarm"

    // 새로운 토큰을 발급받는 경우
    override fun onNewToken(token: String) {
        Log.d(TAG, "new Token: $token")

        /*if(UserManager.userId == null) {
            Log.e(TAG, "onNewToken userId is null")

        } else {
            val json = "{\n" +
                    "    \"user_id\": "+UserManager.userId!!.toString()+",\n" +
                    "    \"fcm_token\": \""+token+"\"\n" +
                    "}"
            val body: RequestBody = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            /*
            val body = FormBody.Builder()
                .add("user_id", )
                .add("fcm_token", )
                .build()
             */
            val request = Request.Builder()
                .url("https://app.api.service.cashcarplus.com:50193/user/fcm")
                .post(body)
                .build()
            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    Log.d(TAG, "onNewToken Update Success : " + response.body.toString())
                }

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    Log.e(TAG, "onNewToken Update Failed : " + e.localizedMessage)
                }
            })
        }*/
    }

    /**
     * this method will be triggered every time there is new FCM Message.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: " + remoteMessage!!.from)

        if(remoteMessage.notification != null) {
            val intent = Intent()
            intent.action = "com.package.notification"
            sendBroadcast(intent)

            sendNotificationForeground(remoteMessage)
        } else if(remoteMessage.data.isNotEmpty()){
            Log.i("바디: ", remoteMessage.data["body"].toString())
            Log.i("타이틀: ", remoteMessage.data["title"].toString())
            sendNotificationWithData(remoteMessage)
        } else {
            Log.i("수신에러: ", "data가 비어있습니다. 메시지를 수신하지 못했습니다.")
            Log.i("data값: ", remoteMessage.data.toString())
        }
    }

    private fun sendNotificationForeground(remoteMessage: RemoteMessage) {
        // RequestCode, Id를 고유값으로 지정하여 알림이 개별 표시되도록 함
        val uniId: Int = (System.currentTimeMillis() / 7).toInt()

        // 일회용 PendingIntent
        // PendingIntent : Intent 의 실행 권한을 외부의 어플리케이션에게 위임한다.
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(this, uniId, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        // 알림 채널 이름
        val channelId = "CashcarplusNotification"

        // 알림 소리
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // 알림에 대한 UI 정보와 작업을 지정한다.
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.appcion_foreground) // 아이콘 설정
                //.setContentTitle(remoteMessage.data["title"].toString())
                .setContentText(remoteMessage.notification!!.body+"") // 메시지 내용
                .setAutoCancel(true)
                .setSound(soundUri) // 알림 소리
                .setContentIntent(pendingIntent) // 알림 실행 시 Intent

        val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 오레오 버전 이후에는 채널이 필요하다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notice", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 생성
        notificationManager.notify(uniId, notificationBuilder.build())
    }

    private fun sendNotificationWithData(remoteMessage: RemoteMessage) {
        // RequestCode, Id를 고유값으로 지정하여 알림이 개별 표시되도록 함
        val uniId: Int = (System.currentTimeMillis() / 7).toInt()

        // 일회용 PendingIntent
        // PendingIntent : Intent 의 실행 권한을 외부의 어플리케이션에게 위임한다.
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Activity Stack 을 경로만 남긴다. A-B-C-D-B => A-B
        val pendingIntent = PendingIntent.getActivity(this, uniId, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        // 알림 채널 이름
        val channelId = "CashcarplusNotification"

        // 알림 소리
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // 알림에 대한 UI 정보와 작업을 지정한다.
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.appcion_foreground) // 아이콘 설정
            //.setContentTitle(remoteMessage.data["title"].toString()) // 제목
            .setContentText(remoteMessage.data["body"].toString()) // 메시지 내용
            .setAutoCancel(true)
            .setSound(soundUri) // 알림 소리
            .setContentIntent(pendingIntent) // 알림 실행 시 Intent

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 오레오 버전 이후에는 채널이 필요하다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notice", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 생성
        notificationManager.notify(uniId, notificationBuilder.build())
    }
}