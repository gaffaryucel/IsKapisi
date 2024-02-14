package com.androiddevelopers.freelanceapp.util

import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

object Util {
    const val DATABASE_URL =
        "https://freelance-app-48c7e-default-rtdb.europe-west1.firebasedatabase.app/"
}

//String öğelerin sonuna fonksiyon ile snackbar çıkarma özelliği sağlar
fun String.snackbar(view: View, duration: Int = Toast.LENGTH_SHORT): Snackbar {
    return Snackbar.make(view, this, duration).apply { show() }
}

