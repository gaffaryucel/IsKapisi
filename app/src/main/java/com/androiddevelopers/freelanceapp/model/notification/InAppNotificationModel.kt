package com.androiddevelopers.freelanceapp.model.notification

class InAppNotificationModel {

    var userId : String? = null
    var notificationId : String? = null
    var title : String? = null
    var message : String? = null
    var userImage : String? = null
    var imageUrl : String? = null
    var userToken : String? = null
    constructor()
    constructor(
        userId : String? = null,
        notificationId : String? = null,
        title : String? = null,
        message : String? = null,
        userImage : String? = null,
        imageUrl : String? = null,
        userToken : String? = null
    ){
        this.userId = userId
        this.notificationId = notificationId
        this.title = title
        this.message = message
        this.userImage = userImage
        this.imageUrl = imageUrl
        this.userToken = userToken
    }
}