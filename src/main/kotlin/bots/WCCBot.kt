package bots

import com.vdurmont.emoji.EmojiParser
import dao.FilmeDao
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendAudio
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup.MEDIA_FIELD
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia.MEDIA_FIELD
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.MessageEntity
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.media.InputMedia
import org.telegram.telegrambots.meta.api.objects.media.InputMedia.MEDIA_FIELD
import org.telegram.telegrambots.meta.api.objects.media.InputMediaDocument
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.awt.Font.ITALIC
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import javax.ws.rs.POST


val TOKEN_TELEGRAM = System.getenv("TOKEN_TELEGRAM")

//git remote get-url origin
class WCCBot : TelegramLongPollingBot() {

    override fun getBotUsername(): String {
        //return bot username
        // If bot username is @HelloKotlinBot, it must return
        return "Botzinho"
    }

    override fun getBotToken(): String {
        // Return bot token from BotFather
        return TOKEN_TELEGRAM
    }

    override fun onUpdateReceived(update: Update?) {
        // We check if the update has a message and the message has text
        val nameSender = update?.message?.from?.firstName
        val chatId = update?.message?.chatId.toString()
        val messageCommand = update?.message?.text
        val filmesDao = FilmeDao()

        var commandID = messageCommand.toString().replace("/", "")

        try {

            when (messageCommand) {

                "/musica" -> execute(
                    SendAudio().apply {
                        val file = File("src/main/resources/Marvel intro.mp3")
                        this.chatId = chatId
                        this.title = "Marvel Abertura"
                        this.caption = welcome(nameSender)
                        this.audio = InputFile().setMedia(file)
                        this.parseMode = "MarkdownV2"
                    }
                )

                "/info" -> execute(
                    SendDocument().apply {
                        this.chatId = chatId
                        this.caption = "Ignorei"
                        this.document = InputFile().setMedia("https://c.tenor.com/IlVw6bsXQJYAAAAC/thumbs-up-hulk.gif")
//                        this.replyMarkup = teclado(ReplyKeyboardMarkup())
                    }
                )
                "/start" -> execute(
                    SendDocument().apply {
                        this.chatId = chatId
                        this.caption = welcome(nameSender)
                        this.document =
                            InputFile().setMedia("https://c.tenor.com/Y5BafazlT0sAAAAd/hello-are-you-still-there.gif")
                        this.parseMode = "MarkdownV2"
                        this.replyMarkup = teclado(ReplyKeyboardMarkup())
                    }

                )

                "/filmes" -> execute(
                    SendDocument().apply {
                        this.chatId = chatId
                        this.caption = listarFilmes()
                        this.document = InputFile().setMedia("https://c.tenor.com/X7r0EeyEUwcAAAAC/avengers-infinity-war.gif")
                        this.parseMode = "MarkdownV2"
                    }
                )

                "/proximos" -> execute(
                    SendDocument().apply {
                        this.chatId = chatId
                        this.caption = listarProxFilmes(filmesDao)
                        this.document =
                            InputFile().setMedia("https://c.tenor.com/BDfaxXA3WfAAAAAd/thanos-thanos-dance.gif")
                        this.parseMode = "MarkdownV2"
                    }

                )

                else -> {
                    if (commandID.toIntOrNull() in 1..30) {
                        execute(
                            SendDocument().apply {
                                this.chatId = chatId
                                this.caption = mostrarFilme(filmesDao, commandID)
                                this.document = buscarAquivo(filmesDao, commandID)
                                this.parseMode = "MarkdownV2"
                            }
                        )
                    } else {
                        execute(
                            SendDocument().apply {
                                this.chatId = chatId
                                this.caption =
                                    "Todo mundo falha em ser quem deveriam ser, Thor\\. O valor de uma pessoa, de um herói… é o quanto eles dão certo em ser quem são\\. \\- *Frigga* \n\n Até eu falho \uD83D\uDE2D "
                                this.document =
                                    InputFile().setMedia("https://c.tenor.com/qfogNHCcvmcAAAAC/sad-thor.gif")
                                this.parseMode = "MarkdownV2"
                            }
                        )
                    }
                }
            }
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    private fun teclado(replyKeyboardMarkup: ReplyKeyboardMarkup): ReplyKeyboard? {
        replyKeyboardMarkup.apply {
            this.selective = true
            this.resizeKeyboard = true
            this.oneTimeKeyboard = false
        }
        var teclado: List<KeyboardRow> = ArrayList()
        val keyboardFirstRow = KeyboardRow()
        keyboardFirstRow.add("/start")
        keyboardFirstRow.add("/filmes")
        val keyboardSecondRow = KeyboardRow()
        keyboardSecondRow.add("/musica")
        keyboardSecondRow.add("/proximos")
        (teclado as ArrayList).add(keyboardFirstRow)
        teclado.add(keyboardSecondRow)
        replyKeyboardMarkup.keyboard = teclado

        return replyKeyboardMarkup
    }


    private fun listarProxFilmes(filmesDao: FilmeDao): String {
        return "✨✨ *Proximos Lançamentos *✨✨\n\n" + filmesDao.listFilmeProx.map { "\\- *${it.nomeFilme}* \\(${it.estreia}\\) \n" }
            .joinToString(separator = "\r\n")
    }

    private fun buscarAquivo(filmesDao: FilmeDao, commandID: String): InputFile {
        val id = commandID.toInt()
        val image = filmesDao.findById(id)?.imagem
        return InputFile().setMedia(File(image))
    }

    private fun mostrarFilme(filmesDao: FilmeDao, commandID: String): String {
        val id = commandID.toInt()
        val filmeCopy = filmesDao.findById(id)?.copy()

        return """
            *${filmeCopy?.nomeFilme}*

            Ano Cronológico: ${filmeCopy?.ano}

            Sinopse: ${filmeCopy?.sinopse}
        """.trimIndent()

    }

    private fun listarFilmes() = """

        \/1 \- *Capitão América\: O Primeiro Vingador* \(1943\-1945\)
        \/2 \- *Capitã Marvel* \(1995\)
        \/3 \- *Homem de Ferro* \(2010\)
        \/4 \- *O Incrível Hulk* \(2011\)
        \/5 \- *Homem de Ferro 2* \(2011\)
        \/6 \- *Thor* \(2011\)
        \/7 \- *Os Vingadores* \(2012\)
        \/8 \- *Homem de Ferro 3* \(2012\)
        \/9 \- *Thor\: O Mundo Sombrio* \(2013\)
        \/10 \- *Capitão América\: O Soldado Invernal* \(2014\)
        \/11 \- *Guardiões da Galáxia* \(2014\)
        \/12 \- *Guardiões da Galáxia Vol\. 2* \(2014\)
        \/13 \- *Vingadores\: Era de Ultron* \(2015\)
        \/14 \- *Homem\-Formiga* \(2015\)
        \/15 \- *Capitão América\: Guerra Civil* \(2016\)
        \/16 \- *Homem\-Aranha\: De Volta ao Lar* \(2016\)
        \/17 \- *Viúva Negra* \(2016\-2017\)
        \/18 \- *Doutor Estranho* \(2016\-2017\)
        \/19 \- *Pantera Negra* \(2017\)
        \/20 \- *Homem\-Formiga e a Vespa* \(2017\)
        \/21 \- *Thor\: Ragnarok* \(2017\)
        \/22 \- *Vingadores\: Guerra Infinita* \(2017\)
        \/23 \- *Vingadores\: Ultimato* \(2018\-2023\)
        \/24 \- *Homem\-Aranha\: Longe de Casa* \(2023\)
        \/25 \- *Wandavision* \(2023\)
        \/26 \- *Falcão e o Soldado Invernal* \(2023\)
        \/27 \- *Shang\-Chi e a Lenda dos Dez Anéis* \(2024\)
        \/28 \- *Eternos* \(2024\)
        \/29 \- *Loki* \(Não definido\)
        \/30 \- *What If\.\.\.\?* \(Não definido\)
    """.trimIndent()

    private fun welcome(nameSender: String?) = EmojiParser.parseToUnicode(
        """ 
        Oi, seja bem vindo\(a\) $nameSender
        
        \/start \- inicio
        
        \/filmes \- mostrar ordem cronologica
        
        \/proximos \- mostrar lista dos próximos lançamentos
        
        \/musica \- tocar abertura

        \/info \- para saber mais sobre o projeto
        
        🕷️ *Com grandes poderes, vêm grandes responsabilidades* 🕷️
        """.trimIndent()
    )


}


