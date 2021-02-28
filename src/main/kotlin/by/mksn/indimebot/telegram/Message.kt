package by.mksn.indimebot.telegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    @SerialName("message_id") val messageId: Long,
    val text: String? = null,
    val from: TelegramUser? = null,
    val date: Int,
    val chat: Chat,
    val entities: List<MessageEntity> = emptyList()
)