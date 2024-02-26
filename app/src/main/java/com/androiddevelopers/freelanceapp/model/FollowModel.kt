package com.androiddevelopers.freelanceapp.model

class FollowModel {
    var userId : String? = null
    var userName : String? = null
    var userImage : String? = null
    constructor()

    constructor(
        userId : String? = null,
        userName : String? = null,
        userImage : String? = null
    ){
        this.userId = userId
        this.userName = userName
        this.userImage = userImage
    }

}