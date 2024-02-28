package com.androiddevelopers.freelanceapp.model

class DiscoverPostModel {
    var postId: String? = null // Gönderinin benzersiz kimliği
    var postOwner: String? = null //Gönderi Sahibinin ID'si
    var description: String? = null // Gönderinin açıklaması
    var tags: List<String>? = null // Gönderinin etiketleri
    var images: List<String>? = null // Gönderinin göreslleri
    var datePosted: String? = null // Gönderinin yayımlandığı tarih
    var ownerName: String? = null
    var ownerImage: String? = null
    var ownerToken: String? = null
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
        ownerToken: String? = null,
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
        this.ownerToken = ownerToken
        this.likeCount = likeCount
        this.comments = comments
    }

    override fun equals(other: Any?): Boolean {
        return this === other &&
                this.postOwner == other.postOwner &&
                this.description == other.description &&
                this.tags?.toTypedArray().contentEquals(
                    other.tags?.toTypedArray()
                ) &&
                this.images?.toTypedArray().contentEquals(
                    other.images?.toTypedArray()
                ) &&
                this.datePosted == other.datePosted &&
                this.ownerName == other.ownerName &&
                this.ownerImage == other.ownerImage &&
                this.ownerToken == other.ownerToken &&
                this.likeCount?.toTypedArray().contentEquals(
                    other.likeCount?.toTypedArray()
                ) &&
                this.postOwner == other.postOwner
    }

    override fun hashCode(): Int {
        var result = postId?.hashCode() ?: 0
        result = 31 * result + (postOwner?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (tags?.hashCode() ?: 0)
        result = 31 * result + (images?.hashCode() ?: 0)
        result = 31 * result + (datePosted?.hashCode() ?: 0)
        result = 31 * result + (ownerName?.hashCode() ?: 0)
        result = 31 * result + (ownerImage?.hashCode() ?: 0)
        result = 31 * result + (ownerToken?.hashCode() ?: 0)
        result = 31 * result + (likeCount?.hashCode() ?: 0)
        result = 31 * result + (comments?.hashCode() ?: 0)
        return result
    }
}