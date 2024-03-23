package com.androiddevelopers.freelanceapp.model.notification

import com.androiddevelopers.freelanceapp.util.NotificationType

class InAppNotificationModel {

    var userId : String? = null
    var notificationType : NotificationType? = null
    var notificationId : String? = null
    var title : String? = null
    var message : String? = null
    var userImage : String? = null
    var imageUrl : String? = null
    var userToken : String? = null
    var time : String? = null
    var idForAction : String? = null
    constructor()
    constructor(
        userId : String? = null,
        notificationType : NotificationType? = null,
        notificationId : String? = null,
        title : String? = null,
        message : String? = null,
        userImage : String? = null,
        imageUrl : String? = null,
        userToken : String? = null,
        time : String? = null,
        idForAction : String? = null
    ){
        this.userId = userId
        this.notificationType = notificationType
        this.notificationId = notificationId
        this.title = title
        this.message = message
        this.userImage = userImage
        this.imageUrl = imageUrl
        this.userToken = userToken
        this.time = time
        this.idForAction = idForAction
    }
}