package by.mksn.indimebot.bot.command

import by.mksn.indimebot.AppContext
import by.mksn.indimebot.misc.TokenGenerator
import by.mksn.indimebot.misc.hashSHA256
import by.mksn.indimebot.telegram.MessageEntity
import by.mksn.indimebot.telegram.TelegramUser
import by.mksn.indimebot.user.User

object StartCommandHandler : BotCommandHandler {
    override val command: String = "/start"
    override suspend fun handle(
        context: AppContext,
        user: TelegramUser,
        text: String,
        entities: Map<String, MessageEntity>
    ) {
        var token: String
        var hash: String
        do {
            token = TokenGenerator.generate()
            hash = token.hashSHA256()
        } while (context.userStore.findByHash(hash) != null)

        val newUser = User(user.id, user.fullName(), hash, user.username, null)
        val message = if (context.userStore.upsert(newUser)) {
            context.messages.command("start", "new_token" to token, "app_url" to context.appUrl)
        } else {
            context.messages.error("update-failed")
        }
        context.sender.sendText(user.id, message, webPagePreview = false)
    }
}