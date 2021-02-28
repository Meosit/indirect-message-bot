package by.mksn.indimebot.telegram

import kotlinx.serialization.Serializable

@Serializable
data class MessageEntity(
    val type: String,
    val offset: Int,
    val length: Int,
    val user: TelegramUser? = null
)
