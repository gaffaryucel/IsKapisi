package com.androiddevelopers.freelanceapp.model

class CommentModel {
    var commentId: String? = null
    var comment: String? = null
    var ownerId: String? = null
    var ownerPhoto: String? = null
    var ownerName: String? = null
    var timestamp: String? = null

    constructor()
    constructor(
        commentId: String? = null,
        comment: String? = null,
        ownerId: String? = null,
        ownerPhoto: String? = null,
        ownerName: String? = null,
        timestamp: String? = null,
    ) {
        this.commentId = commentId
        this.comment = comment
        this.ownerId = ownerId
        this.ownerPhoto = ownerPhoto
        this.ownerName = ownerName
        this.timestamp = timestamp
    }

    override fun equals(other: Any?): Boolean {
        return this === other &&
                this.comment == other.comment &&
                this.ownerId == other.comment &&
                this.ownerPhoto == other.ownerPhoto &&
                this.ownerName == other.ownerName &&
                this.timestamp == other.timestamp
    }

    override fun hashCode(): Int {
        var result = commentId?.hashCode() ?: 0
        result = 31 * result + (comment?.hashCode() ?: 0)
        result = 31 * result + (ownerId?.hashCode() ?: 0)
        result = 31 * result + (ownerPhoto?.hashCode() ?: 0)
        result = 31 * result + (ownerName?.hashCode() ?: 0)
        result = 31 * result + (timestamp?.hashCode() ?: 0)
        return result
    }
}