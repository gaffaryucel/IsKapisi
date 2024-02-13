package com.androiddevelopers.freelanceapp.model

class PreChatModel {
    var postId: String? = null
    var sender: String? = null
    var receiver: String? = null
    var receiverName: String? = null
    var receiverImage: String? = null
    var lastMessage: String? = null
    var timestamp: Long? = null

    constructor(
        postId: String? = null,
        sender: String? = null,
        receiver: String? = null,
        receiverName: String? = null,
        receiverImage: String? = null,
        lastMessage: String? = null,
        timestamp: Long? = null
    ) {
        this.postId = postId
        this.sender = sender
        this.receiver = receiver
        this.receiverName = receiverName
        this.receiverImage = receiverImage
        this.lastMessage = lastMessage
        this.timestamp = timestamp
    }
}