package com.androiddevelopers.freelanceapp.util

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun checkPermissionImageGallery(activity: Activity, requestCode: Int): Boolean {
    val currentPermission = chooseImagePermission()
    return if (ContextCompat.checkSelfPermission(
            activity,
            currentPermission
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        true
    } else {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(currentPermission),
            requestCode
        )
        false
    }

}

fun chooseImagePermission(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.READ_MEDIA_IMAGES
    } else {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    }
}