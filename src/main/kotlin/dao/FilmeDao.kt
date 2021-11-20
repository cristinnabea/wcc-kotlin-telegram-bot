package dao

import model.Filme
import java.io.File
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import model.ProxFilmes

class FilmeDao {
    val fileJsonFilmes : String = File("E:\\Bioca\\WCC\\wcc-kotlin-telegram-bot\\src\\main\\resources\\json\\filmes.json").readText(Charsets.UTF_8)
    val fileJsonProxFilmes : String = File("E:\\Bioca\\WCC\\wcc-kotlin-telegram-bot\\src\\main\\resources\\json\\proximos_filmes.json").readText(Charsets.UTF_8)


    val mapper = jacksonObjectMapper()

    val listFilme : List<Filme> = mapper.readValue(fileJsonFilmes)

    val listFilmeProx : List<ProxFilmes> = mapper.readValue(fileJsonProxFilmes)

    fun findById(id: Int): Filme? {
        return listFilme.find { it.id == id  }
    }

    fun findByFilm(nomeFilme: String): Filme? {
        return listFilme.find { it.nomeFilme == nomeFilme  }
    }
}