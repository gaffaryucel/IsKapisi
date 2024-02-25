package com.androiddevelopers.freelanceapp.model.notification


data class PushNotification(
    val data: NotificationData,
    val to: String
)