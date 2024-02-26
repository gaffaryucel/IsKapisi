package com.androiddevelopers.freelanceapp.model

class PreChatModel {
    var postId: String? = null
    var postType: String? = null
    var sender: String? = null
    var receiver: String? = null
    var receiverName: String? = null
    var receiverImage: String? = null
    var lastMessage: String? = null
    var timestamp: String? = null

    constructor()

    constructor(
        postId: String? = null,
        postType: String? = null,
        sender: String? = null,
        receiver: String? = null,
        receiverName: String? = null,
        receiverImage: String? = null,
        lastMessage: String? = null,
        timestamp: String? = null
    ) {
        this.postId = postId
        this.postType = postType
        this.sender = sender
        this.receiver = receiver
        this.receiverName = receiverName
        this.receiverImage = receiverImage
        this.lastMessage = lastMessage
        this.timestamp = timestamp
    }

    override fun equals(other: Any?): Boolean {
        return this === other &&
                this.postType == other.postType &&
                this.sender == other.sender &&
                this.receiver == other.receiver &&
                this.receiverName == other.receiverName &&
                this.receiverImage == other.receiverImage &&
                this.lastMessage == other.lastMessage &&
                this.timestamp == other.timestamp
    }

    override fun hashCode(): Int {
        var result = postId?.hashCode() ?: 0
        result = 31 * result + (postType?.hashCode() ?: 0)
        result = 31 * result + (sender?.hashCode() ?: 0)
        result = 31 * result + (receiver?.hashCode() ?: 0)
        result = 31 * result + (receiverName?.hashCode() ?: 0)
        result = 31 * result + (receiverImage?.hashCode() ?: 0)
        result = 31 * result + (lastMessage?.hashCode() ?: 0)
        result = 31 * result + (timestamp?.hashCode() ?: 0)
        return result
    }
}