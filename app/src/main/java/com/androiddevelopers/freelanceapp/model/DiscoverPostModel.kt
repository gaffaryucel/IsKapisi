package com.androiddevelopers.freelanceapp.model

import com.androiddevelopers.freelanceapp.util.JobStatus

class DiscoverPostModel {
    var postId: String? = null // Gönderinin benzersiz kimliği
    var postOwner: String? = null //Gönderi Sahibinin ID'si
    var description: String? = null // Gönderinin açıklaması
    var tags: List<String>? = null // Gönderinin etiketleri
    var images: List<String>? = null // Gönderinin göreslleri
    var datePosted: String? = null // Gönderinin yayımlandığı tarih

    constructor()
    constructor(
        postId: String? = null,
        postOwner: String? = null,
        description: String? = null,
        tags: List<String>? = null,
        images: List<String>? = null,
        datePosted: String? = null,
    ) {
        this.postId = postId
        this.postOwner = postOwner
        this.description = description
        this.tags = tags
        this.images = images
        this.datePosted = datePosted
    }
}
data class CommentModel(
    val commentId : String,
    val comment : String,
    val ownerId : String,
    val ownerPhoto : String,
    val ownerName : String,
)