package com.ixsvf.almanaboca.services.repository

import com.ixsvf.almanaboca.services.model.YouTubeResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.gson.gson

class MeditationRepository {

    // 1. Configuração do Cliente Ktor
    // Definimos que vamos usar o engine CIO e o conversor GSON
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            gson() // Configura o Gson automaticamente
        }
    }

    // 2. Função de Pedido (Sem Interface!)
    suspend fun getMeditations(playlistId: String, apiKey: String): YouTubeResponse {
        val url = "https://www.googleapis.com/youtube/v3/playlistItems"

        // Fazemos o GET e passamos os parâmetros de forma segura
        val response: YouTubeResponse = client.get(url) {
            parameter("part", "snippet")
            parameter("maxResults", "50")
            parameter("playlistId", playlistId)
            parameter("key", apiKey)
        }.body() // .body() converte automaticamente o JSON para a tua classe YouTubeResponse

        return response
    }
}