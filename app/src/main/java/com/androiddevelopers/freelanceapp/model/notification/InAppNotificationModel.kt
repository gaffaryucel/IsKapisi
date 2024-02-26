package com.androiddevelopers.freelanceapp.model.notification

class InAppNotificationModel {

    var title : String? = null
    var message : String? = null
    var userImage : String? = null
    var imageUrl : String? = null
    var userToken : String? = null
    constructor()
    constructor(
        title : String? = null,
        message : String? = null,
        userImage : String? = null,
        imageUrl : String? = null,
        userToken : String? = null
    ){
        this.title = title
        this.message = message
        this.userImage = userImage
        this.imageUrl = imageUrl
        this.userToken = userToken
    }
}