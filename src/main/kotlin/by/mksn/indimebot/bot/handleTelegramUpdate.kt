package by.mksn.indimebot.bot

import by.mksn.indimebot.AppContext
import by.mksn.indimebot.bot.command.BotCommandHandler
import by.mksn.indimebot.telegram.Update
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Update.handle")

suspend fun Update.handle(context: AppContext) {
    val entities = message?.entities?.map { it.type to it }?.toMap()
    val commandEntity = entities?.get("bot_command")
    if (message?.text != null && message.from != null && commandEntity != null) {
        val botCommand = message.text.substring(commandEntity.offset, commandEntity.offset + commandEntity.length)
        val handler = BotCommandHandler.handlerOrNull(botCommand)
        logger.info("Handling '$botCommand' command for ${message.from.id}")
        handler
            ?.handle(context, message.from, message.text, entities)
            ?: context.sender.sendText(message.from.id, context.messages.error("no-such-command"))
    } else {
        logger.info("Received non-command update")
    }
}