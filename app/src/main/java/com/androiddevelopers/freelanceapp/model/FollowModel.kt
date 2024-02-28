package com.androiddevelopers.freelanceapp.model

class FollowModel {
    var userId: String? = null
    var userName: String? = null
    var userImage: String? = null

    constructor()

    constructor(
        userId: String? = null,
        userName: String? = null,
        userImage: String? = null
    ) {
        this.userId = userId
        this.userName = userName
        this.userImage = userImage
    }

    override fun equals(other: Any?): Boolean {
        return this === other &&
                this.userName == other.userName &&
                this.userImage == other.userImage
    }

    override fun hashCode(): Int {
        var result = userId?.hashCode() ?: 0
        result = 31 * result + (userName?.hashCode() ?: 0)
        result = 31 * result + (userImage?.hashCode() ?: 0)
        return result
    }

}