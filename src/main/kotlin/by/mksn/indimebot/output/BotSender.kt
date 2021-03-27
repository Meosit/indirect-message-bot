package by.mksn.indimebot.output

import by.mksn.indimebot.misc.trimToLength
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.content.*

const val maxBotOutputLength = 4096
const val maxBotCaptionLength = 1024

class BotSender(private val httpClient: HttpClient, apiToken: String) {

    private val apiUrl = "https://api.telegram.org/bot$apiToken"

    suspend fun sendText(userId: Long, markdown: String, webPagePreview: Boolean = true) {
        httpClient.post<String> {
            url("$apiUrl/sendMessage")
            parameter("text", markdown.trimToLength(maxBotOutputLength, "... (Too long message)"))
            parameter("parse_mode", "Markdown")
            parameter("disable_web_page_preview", !webPagePreview)
            parameter("chat_id", userId)
        }
    }

    suspend fun sendImage(userId: Long, file: PartData.FileItem, markdown: String? = null) {
        val formData = formData { append("photo", InputProvider(block = file.provider), file.headers) }
        httpClient.submitFormWithBinaryData<String>(formData) {
            url("$apiUrl/sendPhoto")
            markdown?.let {
                parameter("caption", it.trimToLength(maxBotCaptionLength, "... (Too long message)"))
                parameter("parse_mode", "Markdown")
            }
            parameter("chat_id", userId)
        }
    }

    suspend fun sendFile(userId: Long, file: PartData.FileItem, markdown: String? = null) {
        val formData = formData { append("document", InputProvider(block = file.provider), file.headers) }
        httpClient.submitFormWithBinaryData<String>(formData) {
            url("$apiUrl/sendDocument")
            markdown?.let {
                parameter("caption", it.trimToLength(maxBotCaptionLength, "... (Too long message)"))
                parameter("parse_mode", "Markdown")
            }
            parameter("chat_id", userId)
        }
    }

}