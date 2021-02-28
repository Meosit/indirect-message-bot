package by.mksn.indimebot.output

import by.mksn.indimebot.misc.trimToLength
import io.ktor.client.*
import io.ktor.client.request.*

class BotSender(private val httpClient: HttpClient, apiToken: String) {

    private val maxOutputLength = 4096

    private val apiUrl = "https://api.telegram.org/bot$apiToken"

    suspend fun sendText(userId: Long, markdown: String, webPagePreview: Boolean = true) {
        httpClient.post<String> {
            url("$apiUrl/sendMessage")
            parameter("text", markdown.trimToLength(maxOutputLength, "... (Too long message)"))
            parameter("parse_mode", "Markdown")
            parameter("disable_web_page_preview", !webPagePreview)
            parameter("chat_id", userId)
        }
    }

}