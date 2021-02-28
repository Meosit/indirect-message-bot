package by.mksn.indimebot.bot.command

import by.mksn.indimebot.AppContext
import by.mksn.indimebot.telegram.MessageEntity
import by.mksn.indimebot.telegram.TelegramUser

object HelpCommandHandler : BotCommandHandler {
    override val command: String = "/help"
    override suspend fun handle(
        context: AppContext,
        user: TelegramUser,
        text: String,
        entities: Map<String, MessageEntity>
    ) {
        val message = context.messages.command("help", "app_url" to context.appUrl)
        context.sender.sendText(user.id, message, webPagePreview = false)
    }
}