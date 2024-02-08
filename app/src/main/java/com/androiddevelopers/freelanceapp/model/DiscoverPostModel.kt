package com.androiddevelopers.freelanceapp.model

import com.androiddevelopers.freelanceapp.util.JobStatus

class DiscoverPostModel {
    var postId: String? = null // Gönderinin benzersiz kimliği
    var postOwner: String? = null //Gönderi Sahibinin ID'si
    var description: String? = null // Gönderinin açıklaması
    var tags: List<String>? = null // Gönderinin etiketleri
    var images: List<String>? = null // Gönderinin göreslleri
    var datePosted: String? = null // Gönderinin yayımlandığı tarih
    var ownerName: String? = null
    var ownerImage: String? = null
    var likeCount: List<String>? = null
    var comments: List<CommentModel>? = null

    constructor()
    constructor(
        postId: String? = null,
        postOwner: String? = null,
        description: String? = null,
        tags: List<String>? = null,
        images: List<String>? = null,
        datePosted: String? = null,
        ownerName: String? = null,
        ownerImage: String? = null,
        likeCount: List<String>? = null,
        comments: List<CommentModel>? = null,
    ) {
        this.postId = postId
        this.postOwner = postOwner
        this.description = description
        this.tags = tags
        this.images = images
        this.datePosted = datePosted
        this.ownerName = ownerName
        this.ownerImage = ownerImage
        this.likeCount = likeCount
        this.comments = comments
    }
}
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
}