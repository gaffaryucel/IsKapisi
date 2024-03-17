package com.androiddevelopers.freelanceapp.model

class ChatModel {
    var chatId: String? = null
    var receiverId: String? = null
    var receiverUserName: String? = null
    var receiverUserImage: String? = null
    var chatLastMessage: String? = null
    var chatLastMessageTimestamp: String? = null
    var seen: Boolean? = null

    constructor()

    constructor(
        chatId: String? = null,
        receiverId: String? = null,
        receiverUserName: String? = null,
        receiverUserImage: String? = null,
        chatLastMessage: String? = null,
        chatLastMessageTimestamp: String? = null,
        seen: Boolean? = null,
    ) {
        this.chatId = chatId
        this.receiverId = receiverId
        this.receiverUserName = receiverUserName
        this.receiverUserImage = receiverUserImage
        this.chatLastMessage = chatLastMessage
        this.chatLastMessageTimestamp = chatLastMessageTimestamp
        this.seen = seen
    }

    override fun equals(other: Any?): Boolean {
        return this === other &&
                this.receiverId == other.receiverId &&
                this.receiverUserName == other.receiverUserName &&
                this.receiverUserImage == other.receiverUserImage &&
                this.chatLastMessage == other.chatLastMessage &&
                this.chatLastMessageTimestamp == other.chatLastMessageTimestamp &&
                this.seen == other.seen

    }

    override fun hashCode(): Int {
        var result = chatId?.hashCode() ?: 0
        result = 31 * result + (receiverId?.hashCode() ?: 0)
        result = 31 * result + (receiverUserName?.hashCode() ?: 0)
        result = 31 * result + (receiverUserImage?.hashCode() ?: 0)
        result = 31 * result + (chatLastMessage?.hashCode() ?: 0)
        result = 31 * result + (chatLastMessageTimestamp?.hashCode() ?: 0)
        result = 31 * result + (seen?.hashCode() ?: 0)
        return result
    }

}
