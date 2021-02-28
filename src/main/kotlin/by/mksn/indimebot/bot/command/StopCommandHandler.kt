package by.mksn.indimebot.bot.command

import by.mksn.indimebot.AppContext
import by.mksn.indimebot.telegram.MessageEntity
import by.mksn.indimebot.telegram.TelegramUser

object StopCommandHandler: BotCommandHandler {
    override val command: String = "/stop"

    override suspend fun handle(context: AppContext, user: TelegramUser, text: String, entities: Map<String, MessageEntity>) {
        val foundUser = context.userStore.findByIdOrUsername(user.id)
        if (foundUser == null) {
            context.sender.sendText(user.id, context.messages.error("bot-not-started"))
            return
        }
        val message = if (context.userStore.delete(user.id)) {
            context.messages.command("stop")
        } else {
            context.messages.error("update-failed")
        }
        context.sender.sendText(user.id, message, webPagePreview = false)
    }
}