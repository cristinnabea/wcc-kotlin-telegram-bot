import bots.WCCBot
import dao.FilmeDao
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

fun main() {
    val log: Logger = LoggerFactory.getLogger("main")



    // Initialize Api Context
    val botSession = DefaultBotSession()

    // Instantiate Telegram Bots API
    val botsApi = TelegramBotsApi(botSession::class.java)

    // Register our bot
    try {
        botsApi.registerBot(WCCBot()) //iniciando o bot

        log.info("WCC Bot is up \\o/")
    } catch (e: TelegramApiException) {
        e.printStackTrace()

        log.error("Telegram API failure", e)
    }
}
