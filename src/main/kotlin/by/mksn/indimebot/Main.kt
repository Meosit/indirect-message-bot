package by.mksn.indimebot

import by.mksn.indimebot.bot.handle
import by.mksn.indimebot.misc.escapeMarkdown
import by.mksn.indimebot.misc.hashSHA256
import by.mksn.indimebot.telegram.Update
import by.mksn.indimebot.ui.rootPageHtml
import io.ktor.application.*
import io.ktor.client.features.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.netty.*
import io.ktor.utils.io.*
import org.slf4j.LoggerFactory
import java.io.PrintWriter
import java.io.StringWriter

private val logger = LoggerFactory.getLogger("MainKt")

fun Application.main() {
    val appUrl: String = System.getenv("APP_URL")
    val dbUrl: String = System.getenv("DATABASE_URL")
    val botToken: String = System.getenv("BOT_TOKEN")

    logger.info("app url: $appUrl")
    logger.info("DB url: $dbUrl")
    logger.info("bot token: $botToken")

    val context = AppContext.init(dbUrl, botToken, appUrl)

    install(ContentNegotiation) {
        json(context.json, ContentType.Application.Json)
    }
    install(DefaultHeaders)
    install(Routing) {
        post("/handle/$botToken") {
            try {
                val update = call.receive<Update>()
                update.handle(context)
            } catch (e: Exception) {
                val sw = StringWriter()
                e.printStackTrace(PrintWriter(sw))
                (e as? ClientRequestException)?.response?.content?.let {
                    logger.error(it.readUTF8Line())
                }
                logger.error("Uncaught exception: $sw")
            }
            call.respond(HttpStatusCode.OK)
        }
        post("/send-message") {
            try {
                val parts = call.receiveMultipart().readAllParts()
                val token = (parts.firstOrNull { it.name == "token" } as? PartData.FormItem)?.value
                    ?: throw IllegalArgumentException("No token")
                val message = (parts.firstOrNull { it.name == "message-text" } as? PartData.FormItem)?.value
                val compressImages = (parts.firstOrNull { it.name == "compress-images" } as? PartData.FormItem)?.value.toBoolean()
                val attachment = parts.firstOrNull { it.name == "attachment" } as? PartData.FileItem


                val foundUser = context.userStore.findByHash(token.hashSHA256())
                if (foundUser != null) {
                    when {
                        attachment != null -> {
                            val contentType = attachment.contentType ?: throw IllegalArgumentException("No content type")
                            if (contentType.match(ContentType.Image.Any) && compressImages) {
                                context.sender.sendImage(foundUser.id, attachment, message?.escapeMarkdown())
                            } else {
                                context.sender.sendFile(foundUser.id, attachment, message?.escapeMarkdown())
                            }
                            call.respond(HttpStatusCode.OK)
                        }
                        message != null -> {
                            context.sender.sendText(foundUser.id, message.escapeMarkdown())
                            call.respond(HttpStatusCode.OK)
                        }
                        else -> call.respond(HttpStatusCode.BadRequest)
                    }
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                logger.error("Uncaught exception: ", e)
                call.respond(HttpStatusCode.BadRequest)
            }
        }
        get("/") {
            rootPageHtml()
        }
        static("assets") {
            resources("assets")
        }

    }
}

fun main(args: Array<String>) = EngineMain.main(args)