package com.androiddevelopers.freelanceapp.util

import android.util.Log
import android.view.View
import android.widget.Toast
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.model.notification.InAppNotificationModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot

//String öğelerin sonuna fonksiyon ile snackbar çıkarma özelliği sağlar
fun String.snackbar(view: View, duration: Int = Toast.LENGTH_SHORT): Snackbar {
    return Snackbar.make(view, this, duration).apply { show() }
}

//Modellerdeki değişkenler ile veritabanı değişkenleri arasında uyumsuzluk olursa uygulamanın çökmemesi için eklendi
fun QueryDocumentSnapshot.toUserModel(): UserModel? = try {
    toObject(UserModel::class.java)
} catch (e: Exception) {
    e.message?.let { Log.e("getUserModel", it) }
    UserModel()
}

fun DocumentSnapshot.toUserModel(): UserModel? = try {
    toObject(UserModel::class.java)
} catch (e: Exception) {
    e.message?.let { Log.e("getUserModel", it) }
    UserModel()
}

fun QueryDocumentSnapshot.toFreelancerJobPost(): FreelancerJobPost? = try {
    toObject(FreelancerJobPost::class.java)
} catch (e: Exception) {
    e.message?.let { Log.e("getFreelancerJobPost", it) }
    FreelancerJobPost()
}

fun DocumentSnapshot.toFreelancerJobPost(): FreelancerJobPost? = try {
    toObject(FreelancerJobPost::class.java)
} catch (e: Exception) {
    e.message?.let { Log.e("getFreelancerJobPost", it) }
    FreelancerJobPost()
}

fun QueryDocumentSnapshot.toEmployerJobPost(): EmployerJobPost? = try {
    toObject(EmployerJobPost::class.java)
} catch (e: Exception) {
    e.message?.let { Log.e("getEmployerJobPost", it) }
    EmployerJobPost()
}

fun DocumentSnapshot.toEmployerJobPost(): EmployerJobPost? = try {
    toObject(EmployerJobPost::class.java)
} catch (e: Exception) {
    e.message?.let { Log.e("getEmployerJobPost", it) }
    EmployerJobPost()
}

@Suppress("unused")
fun QueryDocumentSnapshot.toInAppNotificationModel(): InAppNotificationModel? = try {
    toObject(InAppNotificationModel::class.java)
} catch (e: Exception) {
    e.message?.let { Log.e("getInAppNotificationModel", it) }
    InAppNotificationModel()
}

fun DocumentSnapshot.toInAppNotificationModel(): InAppNotificationModel? = try {
    toObject(InAppNotificationModel::class.java)
} catch (e: Exception) {
    e.message?.let { Log.e("getInAppNotificationModel", it) }
    InAppNotificationModel()
}

@Suppress("unused")
fun QueryDocumentSnapshot.toDiscoverPostModel(): DiscoverPostModel? = try {
    toObject(DiscoverPostModel::class.java)
} catch (e: Exception) {
    e.message?.let { Log.e("getDiscoverPostModel", it) }
    DiscoverPostModel()
}

fun DocumentSnapshot.toDiscoverPostModel(): DiscoverPostModel? = try {
    toObject(DiscoverPostModel::class.java)
} catch (e: Exception) {
    e.message?.let { Log.e("getDiscoverPostModel", it) }
    DiscoverPostModel()
}
