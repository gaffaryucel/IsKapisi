package com.androiddevelopers.freelanceapp.model

import com.androiddevelopers.freelanceapp.util.JobStatus

class DiscoverPostModel {
    var postId: String? = null // Gönderinin benzersiz kimliği
    var postOwner: String? = null //Gönderi Sahibinin ID'si
    var title: String? = null // Gönderinin başlığı
    var description: String? = null // Gönderinin açıklaması
    var tags: List<String>? = null // Gönderinin etiketleri
    var images: List<String>? = null // Gönderinin göreslleri
    var datePosted: String? = null // Gönderinin yayımlandığı tarih

    constructor()
    constructor(
        postId: String?,
        postOwner: String?,
        title: String?,
        description: String?,
        tags: List<String>?,
        images: List<String>?,
        datePosted: String?,
    ) {
        this.postId = postId
        this.postOwner = postOwner
        this.title = title
        this.description = description
        this.tags = tags
        this.images = images
        this.datePosted = datePosted
    }
}