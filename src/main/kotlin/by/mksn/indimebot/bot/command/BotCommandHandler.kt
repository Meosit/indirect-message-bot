package by.mksn.indimebot.bot.command

import by.mksn.indimebot.AppContext
import by.mksn.indimebot.telegram.MessageEntity
import by.mksn.indimebot.telegram.TelegramUser

interface BotCommandHandler {
    val command: String
    suspend fun handle(context: AppContext, user: TelegramUser, text: String, entities: Map<String, MessageEntity> = emptyMap())

    companion object {

        private val handlers = listOf(
            HelpCommandHandler,
            StartCommandHandler,
            PassphraseCommandHandler,
            StopCommandHandler,
        )

        fun handlerOrNull(text: String) =
            handlers.find { handler -> text.startsWith(handler.command) }
    }
}