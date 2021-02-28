package by.mksn.indimebot

import by.mksn.indimebot.output.BotSender
import by.mksn.indimebot.user.UserStore
import com.vladsch.kotlin.jdbc.HikariCP
import com.vladsch.kotlin.jdbc.SessionImpl
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.InputStreamReader
import java.net.URI

private val logger = LoggerFactory.getLogger("AppContext")

data class AppContext(
    val botToken: String,
    val appUrl: String,
    val json: Json,
    val httpClient: HttpClient,
    val sender: BotSender,
    val userStore: UserStore,
    val messages: Messages,
) {

    companion object {
        private fun initializeDataSource(dbUrl: String): UserStore {
            val dbUri = URI(dbUrl)
            val (username: String, password: String) = dbUri.userInfo.split(":")
            val jdbcUrl = "jdbc:postgresql://${dbUri.host}:${dbUri.port}${dbUri.path}?sslmode=require"
            val config = HikariConfig().apply {
                setJdbcUrl(jdbcUrl)
                setUsername(username)
                setPassword(password)
                maximumPoolSize = 3
            }
            HikariCP.defaultCustom(HikariDataSource(config))
            SessionImpl.defaultDataSource = { HikariCP.dataSource() }
            logger.info("JDBC url: $jdbcUrl")
            return UserStore.create()
        }

        private fun loadResourceAsString(resourceBaseName: String): String = AppContext::class.java.classLoader
            .getResourceAsStream(resourceBaseName)
            .let { it ?: throw IllegalStateException("Null resource stream for $resourceBaseName") }
            .use { InputStreamReader(it).use(InputStreamReader::readText) }

        fun init(dbUrl: String, botToken: String, appUrl: String): AppContext {
            val json = Json { ignoreUnknownKeys = true }
            val httpClient = HttpClient {
                install(JsonFeature) {
                    serializer = KotlinxSerializer(json)
                }
            }

            val userStore = initializeDataSource(dbUrl)

            val commandMessages = Messages("message", this::loadResourceAsString)

            val botOutputSender = BotSender(httpClient, botToken)

            return AppContext(botToken, appUrl, json, httpClient, botOutputSender, userStore, commandMessages)
        }
    }
}

class Messages(private val basePath: String, private val loader: (String) -> String) {

    fun error(name: String, vararg replacements: Pair<String, String>) =
        loadMessage("error", name, replacements)

    fun command(name: String, vararg replacements: Pair<String, String>) =
        loadMessage("command", name, replacements)

    private fun loadMessage(type: String, name: String, replacements: Array<out Pair<String, String>>) = replacements
        .fold(loader("$basePath/$type/$name.md")) { acc, (key, value) -> acc.replace("{$key}", value) }

}