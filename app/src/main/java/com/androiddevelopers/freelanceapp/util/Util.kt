package com.androiddevelopers.freelanceapp.util

import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

object Util {
    const val DATABASE_URL = "https://freelance-app-48c7e-default-rtdb.europe-west1.firebasedatabase.app/"
    const val BASE_URL = "https://fcm.googleapis.com"
    const val SERVER_KEY = "AAAA15oBBjU:APA91bFUiRiw-DtR2PMfvD9-qVG8W6sp0xk4xv8fwcuFyIThbLZt885oKfQ-fQfe_zNtzXrzWsehkg-0l2BSkmpvGX7kHG0RJ6RW9QXwWBOECoR5s3U3nn_ao7_16CO3F3bDq78T1v-L"
    const val CONTENT_TYPE = "application/json"
    const val TOPIC = "/topics/myTopic2"
}

//String öğelerin sonuna fonksiyon ile snackbar çıkarma özelliği sağlar
fun String.snackbar(view: View, duration: Int = Toast.LENGTH_SHORT): Snackbar {
    return Snackbar.make(view, this, duration).apply { show() }
}

