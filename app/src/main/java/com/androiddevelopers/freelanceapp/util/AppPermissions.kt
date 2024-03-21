package com.androiddevelopers.freelanceapp.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun checkPermissionImageGallery(activity: Activity, requestCode: Int): Boolean {
    val currentPermission = chooseImageMediaPermission()
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

fun checkPermissionImageCamera(activity: Activity, requestCode: Int): Boolean {
    return if (ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        true
    } else {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.CAMERA),
            requestCode
        )
        false
    }
}

private fun chooseImageMediaPermission(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
}