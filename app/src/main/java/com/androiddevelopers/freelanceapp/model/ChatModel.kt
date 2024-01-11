package com.androiddevelopers.freelanceapp.model

class ChatModel{
    var chatId : String? = null
    var messageSender : String? = null
    var messageReceiver: String? = null
    var userName : String? = null
    var userImage : String? = null
    var chatLastMessage : String? = null
    var chatLastMessageTimestamp: String? = null
    constructor()

    constructor(
        chatId : String? = null,
        messageSender : String? = null,
        messageReceiver : String? = null,
        userName : String? = null,
        userImage : String? = null,
        chatLastMessage : String? = null,
        chatLastMessageTimestamp: String? = null,
    ){
        this.chatId = chatId
        this.messageSender = messageSender
        this.messageReceiver = messageReceiver
        this.userName = userName
        this.userImage = userImage
        this.chatLastMessage = chatLastMessage
        this.chatLastMessageTimestamp = chatLastMessageTimestamp
    }
}