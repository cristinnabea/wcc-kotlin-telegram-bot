package model
import kotlinx.serialization.Serializable

@Serializable
data class Filme(val id: Int, val nomeFilme: String, val ano: String, val sinopse: String, val imagem: String)



