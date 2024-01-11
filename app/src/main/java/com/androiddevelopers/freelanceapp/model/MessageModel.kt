package com.androiddevelopers.freelanceapp.model

class MessageModel {

    var messageId : String? = null
    var messageData : String? = null
    var messageSender : String? = null
    var messageReceiver: String? = null
    constructor()

    constructor(
        messageId : String? = null,
        messageData : String? = null,
        messageSender : String? = null,
        messageReceiver : String? = null,
    ){
        this.messageId = messageId
        this.messageData = messageData
        this.messageSender = messageSender
        this.messageReceiver = messageReceiver
    }
}