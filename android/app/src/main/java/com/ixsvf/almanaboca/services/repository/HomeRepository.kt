package com.ixsvf.almanaboca.services.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.ixsvf.almanaboca.services.model.HomeItem
import kotlinx.coroutines.tasks.await

class HomeRepository {

    private val db = FirebaseFirestore.getInstance()
    // Certifique-se que o ID do documento está correto
    private val docRef = db.collection("home_items").document("nkC4lOyD6jZSxBw1k37E")

    suspend fun getHomeItems(): List<HomeItem> {
        return try {
            val snapshot = docRef.get().await()

            if (snapshot.exists()) {
                val listaFinal = mutableListOf<HomeItem>()

                // --- 1. LER ARRAY DE CURSOS (Coaching) ---
                // Lemos como uma lista genérica para não falhar
                val rawCourses = snapshot.get("courses") as? List<Map<String, Any?>>

                rawCourses?.forEach { map ->
                    try {
                        val item = HomeItem(
                            // Lê cada campo com segurança. Se não existir, põe vazio.
                            coachProgram = map["coachProgram"] as? String ?: "",
                            whatToExpectProgram = map["whatToExpectProgram"] as? String ?: "",
                            coachStartDate = map["coachStartDate"]?.toString() ?: "", // Aceita qualquer formato
                            coachDateEnd = map["coachDateEnd"]?.toString() ?: "",

                            // Conversão Segura de Números (String ou Number -> Double/Int)
                            coachDiscount = parseDouble(map["coachDiscount"]),
                            coachVacancies = parseInt(map["coachVacancies"]),

                            instagramUrl = map["instagramUrl"] as? String ?: ""
                        )
                        // Só adiciona se tiver nome do programa (para não mostrar vazios)
                        if (item.coachProgram.isNotEmpty()) {
                            listaFinal.add(item)
                        }
                    } catch (e: Exception) {
                        Log.e("FIREBASE", "Erro ao ler um curso específico: ${e.message}")
                    }
                }

                // --- 2. LER DADOS DA RAIZ (Meditação - Cartão Único) ---
                val medType = snapshot.getString("meditationCirclesType") ?: ""

                // Se o campo meditationCirclesType existir na raiz, criamos o item
                if (medType.isNotEmpty()) {
                    val medItem = HomeItem(
                        id = "meditacao_raiz",
                        meditationCirclesType = medType,
                        meditationCirclesDesc = snapshot.getString("meditationCirclesDesc") ?: "",
                        meditationCirclesNextSession = snapshot.getString("meditationCirclesNextSession") ?: "",
                        meditationCirclesLocation = snapshot.getString("meditationCirclesLocation") ?: "",
                        meditationCirclesPrice = parseDouble(snapshot.get("meditationCirclesPrice"))
                    )
                    listaFinal.add(medItem)
                    Log.d("FIREBASE", "Meditação da raiz adicionada com sucesso.")
                }

                return listaFinal
            }
            emptyList()
        } catch (e: Exception) {
            Log.e("FIREBASE_CRASH", "Erro fatal ao ler documento: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    // --- Funções Auxiliares de Segurança ---
    // Converte qualquer coisa (texto "20.5", número 20, vazio "") para Double
    private fun parseDouble(value: Any?): Double {
        return when (value) {
            is Number -> value.toDouble()
            is String -> value.replace(",", ".").toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
    }

    // Converte qualquer coisa para Int
    private fun parseInt(value: Any?): Int {
        return when (value) {
            is Number -> value.toInt()
            is String -> value.trim().toIntOrNull() ?: 0
            else -> 0
        }
    }
}