package com.androiddevelopers.freelanceapp.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File


fun createImageUri(context: Context): Uri {
    val image = File(context.filesDir, "camera_photos.png")
    return FileProvider.getUriForFile(
        context,
        "com.androiddevelopers.freelanceapp.FileProvider",
        image
    )
}

fun convertUriToBitmap(uri: Uri, activity: Activity): Bitmap {
    val imageBitmap: Bitmap

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val imageSource =
            ImageDecoder.createSource(activity.contentResolver, uri)
        imageBitmap = ImageDecoder.decodeBitmap(imageSource)
    } else {
        @Suppress("DEPRECATION")
        imageBitmap = MediaStore.Images.Media.getBitmap(
            activity.contentResolver,
            uri
        )
    }
    return imageBitmap
}

/*
    // Örnek kullanım
val bitmap = BitmapFactory.decodeResource(resources, R.drawable.my_image)
compressImageInBackground(bitmap,50) { compressedBitmap ->
    // Sıkıştırılmış bitmap ile işlem yapın
}
*/

//PNG resimi arkaplanda sıkıştırmak için
fun compressPngInBackground(bitmap: Bitmap, quality: Int = 80, callback: (ByteArray) -> Unit) {
    CoroutineScope(IO).launch {
        val compressedBitmap = compressPNG(bitmap, quality)
        callback(compressedBitmap)
    }
}

//PNG resim sıkıştırmak için
fun compressPNG(bitmap: Bitmap, quality: Int = 80): ByteArray {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream)
    return outputStream.toByteArray()
}

//JPEG resimi arkaplanda sıkıştırmak için
fun compressJpegInBackground(bitmap: Bitmap, quality: Int = 80, callback: (ByteArray) -> Unit) {
    CoroutineScope(IO).launch {
        val compressedBitmap = compressJPEG(bitmap, quality)
        callback(compressedBitmap)
    }
}

//JPAG resim sıkıştırmak için
fun compressJPEG(bitmap: Bitmap, quality: Int = 80): ByteArray {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    return outputStream.toByteArray()
}