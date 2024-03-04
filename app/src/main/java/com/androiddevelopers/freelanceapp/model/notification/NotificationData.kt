package com.androiddevelopers.freelanceapp.model.notification

import com.androiddevelopers.freelanceapp.util.NotificationTypeForActions

data class NotificationData(
    val title: String,
    val message: String,
    val imageUrl : String,
    val profileImage : String,
    val type : NotificationTypeForActions,
    val preMessageObject : PreMessageObject? = null,
    val messageObject : MessageObject? = null,
    val freelancerPostObject : String? = null,
    val employerPostObject : String? = null,
    val discoverPostObject : String? = null,
    val like : String? = null,
    val comment : String? = null,
    val followObject : String? = null
)
data class MessageObject(
    val chatId : String,
    val receiverId: String,
    val receiverUserName : String,
    val receiverUserImage : String
)
data class PreMessageObject(
    val userId : String,
    val postId: String,
    val type : String
)

