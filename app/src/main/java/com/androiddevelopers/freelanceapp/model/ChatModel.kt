package com.androiddevelopers.freelanceapp.model

class ChatModel{
    var chatId : String? = null
    var receiverId: String? = null
    var receiverUserName : String? = null
    var receiverUserImage : String? = null
    var chatLastMessage : String? = null
    var chatLastMessageTimestamp: String? = null
    constructor()

    constructor(
        chatId : String? = null,
        receiverId : String? = null,
        receiverUserName : String? = null,
        receiverUserImage : String? = null,
        chatLastMessage : String? = null,
        chatLastMessageTimestamp: String? = null,
    ){
        this.chatId = chatId
        this.receiverId = receiverId
        this.receiverUserName = receiverUserName
        this.receiverUserImage = receiverUserImage
        this.chatLastMessage = chatLastMessage
        this.chatLastMessageTimestamp = chatLastMessageTimestamp
    }
}
