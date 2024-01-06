package com.androiddevelopers.freelanceapp.model

class VideoModel {
    var videoId: String? = null
    var userId: String? = null
    var videoUrl: String? = null
    var thumbnailUrl: String? = null
    var title: String? = null
    var description: String? = null
    var likesCount: Int? = null
    var commentsCount: Int? = null
    var timestamp: Long? = null
    var tags: List<String>? = null

    constructor()

    constructor(
        videoId: String?,
        userId: String?,
        videoUrl: String?,
        thumbnailUrl: String?,
        title: String?,
        description: String?,
        likesCount: Int?,
        commentsCount: Int?,
        timestamp: Long?,
        tags: List<String>?,
    ) {
        this.videoId = videoId
        this.userId = userId
        this.videoUrl = videoUrl
        this.thumbnailUrl = thumbnailUrl
        this.title = title
        this.description = description
        this.likesCount = likesCount
        this.commentsCount = commentsCount
        this.timestamp = timestamp
        this.tags = tags
    }
}

