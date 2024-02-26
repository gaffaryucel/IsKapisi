package com.androiddevelopers.freelanceapp.model

class MessageModel {

    var messageId: String? = null
    var messageData: String? = null
    var messageSender: String? = null
    var messageReceiver: String? = null
    var timestamp: String? = null

    constructor()

    constructor(
        messageId: String? = null,
        messageData: String? = null,
        messageSender: String? = null,
        messageReceiver: String? = null,
        timestamp: String? = null,
    ) {
        this.messageId = messageId
        this.messageData = messageData
        this.messageSender = messageSender
        this.messageReceiver = messageReceiver
        this.timestamp = timestamp
    }

    override fun equals(other: Any?): Boolean {
        return this === other &&
                this.messageData == other.messageData &&
                this.messageSender == other.messageSender &&
                this.messageReceiver == other.messageReceiver &&
                this.timestamp == other.timestamp
    }

    override fun hashCode(): Int {
        var result = messageId?.hashCode() ?: 0
        result = 31 * result + (messageData?.hashCode() ?: 0)
        result = 31 * result + (messageSender?.hashCode() ?: 0)
        result = 31 * result + (messageReceiver?.hashCode() ?: 0)
        result = 31 * result + (timestamp?.hashCode() ?: 0)
        return result
    }
}