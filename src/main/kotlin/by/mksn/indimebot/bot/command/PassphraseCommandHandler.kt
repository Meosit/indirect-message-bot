package by.mksn.indimebot.bot.command

import by.mksn.indimebot.AppContext
import by.mksn.indimebot.misc.hashSHA256
import by.mksn.indimebot.telegram.MessageEntity
import by.mksn.indimebot.telegram.TelegramUser

object PassphraseCommandHandler: BotCommandHandler {
    override val command: String = "/passphrase"

    override suspend fun handle(context: AppContext, user: TelegramUser, text: String, entities: Map<String, MessageEntity>) {
        val passphrase = text.removePrefix(command).trim()
        val foundUser = context.userStore.findByIdOrUsername(user.id)
        when {
            foundUser == null ->
                context.sender.sendText(user.id, context.messages.error("bot-not-started"))
            user.username == null ->
                context.sender.sendText(user.id, context.messages.error("passphrase-no-username"))
            !passphrase.startsWith(user.username) ->
                context.sender.sendText(user.id, context.messages.error("passphrase-prefix"))
            else -> {
                val passphraseHash = passphrase.hashSHA256()
                val existingUser = context.userStore.findByHash(passphraseHash)
                if (existingUser == null || existingUser.id == foundUser.id) {
                    val updatedUser = foundUser.copy(
                        name = user.fullName(),
                        username = user.username,
                        passphraseHash = passphraseHash)
                    context.userStore.upsert(updatedUser)
                    val message = if (context.userStore.upsert(updatedUser)) {
                        context.messages.command("passphrase")
                    } else {
                        context.messages.error("update-failed")
                    }
                    context.sender.sendText(user.id, message)
                } else {
                    context.sender.sendText(existingUser.id, context.messages.error("hash-collision"))
                    context.sender.sendText(user.id, context.messages.error("passphrase-not-allowed"))
                }
            }
        }
    }
}