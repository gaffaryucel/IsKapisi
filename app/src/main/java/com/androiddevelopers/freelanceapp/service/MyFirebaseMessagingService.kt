package com.androiddevelopers.freelanceapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.model.notification.MessageObject
import com.androiddevelopers.freelanceapp.model.notification.PreMessageObject
import com.androiddevelopers.freelanceapp.util.NotificationTypeForActions
import com.androiddevelopers.freelanceapp.view.BottomNavigationActivity
import com.androiddevelopers.freelanceapp.view.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random


private const val CHANNEL_ID = "my_channel"
class MyFirebaseMessagingService : FirebaseMessagingService() {



    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        saveToken(newToken)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)



        val sharedPref = applicationContext.getSharedPreferences("notification", Context.MODE_PRIVATE)

        var usersOnlineChatId : String? = ""

        val type = message.data["type"] ?: ""
        sharedPref.edit().putString("not_type", type).apply()
            when(type){
                NotificationTypeForActions.MESSAGE.toString()->{
                    getMessagingObject(message,sharedPref)
                    usersOnlineChatId =sharedPref.getString("chatId", "")
                }
                NotificationTypeForActions.PRE_MESSAGE.toString()->{
                    getPreMessagingObject(message,sharedPref)
                    usersOnlineChatId = sharedPref.getString("postId", "")
                }
                NotificationTypeForActions.FRL_JOB_POST.toString()->{
                    val freelancerPostObject = message.data["freelancerPostObject"] ?: ""
                    sharedPref.edit().putString("freelancerPostObject", freelancerPostObject).apply()
                }
                NotificationTypeForActions.EMP_JOB_POST.toString()->{
                    val employerPostObject = message.data["employerPostObject"] ?: ""
                    sharedPref.edit().putString("employerPostObject", employerPostObject).apply()
                }
                NotificationTypeForActions.LIKE.toString()->{
                    val like = message.data["like"] ?: ""
                    sharedPref.edit().putString("like", like).apply()
                }
                NotificationTypeForActions.COMMENT.toString()->{
                    val comment = message.data["comment"] ?: ""
                    sharedPref.edit().putString("comment", comment).apply()
                }
                NotificationTypeForActions.FOLLOW.toString()->{
                    val followObject = message.data["followObject"] ?: ""
                    sharedPref.edit().putString("followObject", followObject).apply()
                }
            }


        val intent = Intent(this, BottomNavigationActivity::class.java)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // Görselin URL'sini al
        val imageUrl = message.data["imageUrl"]
        val profileImageUrl = message.data["profileImage"]

        // Görseli indir ve bildirimde göstermek için büyük bir stil oluştur
        val bigPictureStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(getBitmapFromUrl(imageUrl)) // Görseli URL'den al ve bitmap olarak indir

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.app_logo)
            .setAutoCancel(true)
            .setLargeIcon(getBitmapFromUrl(profileImageUrl))
            .setContentIntent(pendingIntent)
            .setStyle(bigPictureStyle) // Büyük resim stili
            .build()


        // Bildirim geldiğinde
        val sharedPreferences = applicationContext.getSharedPreferences("chatPage", Context.MODE_PRIVATE)
        val currentChatPageId = sharedPreferences.getString("current_chat_page_id", null)
        if (currentChatPageId != null && currentChatPageId == usersOnlineChatId) {
            // Bildirim, kullanıcının bulunduğu sayfa ile ilişkilendirilmişse, bildirimi gösterme
            return
        } else {
            notificationManager.notify(notificationID, notification)
        }

    }

    // URL'den bitmap olarak görsel indiren bir fonksiyon
    private fun getBitmapFromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(
            CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun saveToken(myToken : String){
        val userCollection = FirebaseFirestore.getInstance().collection("users")
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        GlobalScope.launch(Dispatchers.IO) {
            val tokenMap = hashMapOf<String,Any?>(
                "token" to myToken
            )
            userCollection.document(userId).update(tokenMap)
        }

    }

    private fun getPreMessagingObject(notification: RemoteMessage, sharedPref: SharedPreferences) {
        try {
            val preMessageObjectJson = notification.data["preMessageObject"] ?: ""
            if (preMessageObjectJson.isNotEmpty()) {
                val gson = Gson()
                val preMessageObject = gson.fromJson(preMessageObjectJson, PreMessageObject::class.java)

                sharedPref.edit().putString("userId", preMessageObject.userId).apply()
                sharedPref.edit().putString("postId", preMessageObject.postId).apply()
                sharedPref.edit().putString("type", preMessageObject.type).apply()
            } else {
                println("hata : boş obje")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Hata durumunda uygun bir işlem yapabilirsiniz
        }
    }

    private fun getMessagingObject(notification: RemoteMessage, sharedPref: SharedPreferences) {
        try {
            val preMessageObjectJson = notification.data["preMessageObject"] ?: ""
            if (preMessageObjectJson.isNotEmpty()) {
                val gson = Gson()
                val preMessageObject = gson.fromJson(preMessageObjectJson, MessageObject::class.java)

                sharedPref.edit().putString("chatId", preMessageObject.chatId).apply()
                sharedPref.edit().putString("receiverId", preMessageObject.receiverId).apply()
                sharedPref.edit().putString("receiverUserName", preMessageObject.receiverUserName).apply()
                sharedPref.edit().putString("receiverUserImage", preMessageObject.receiverUserImage).apply()
            } else {
                println("hata : boş obje")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Hata durumunda uygun bir işlem yapabilirsiniz
        }
    }
}

