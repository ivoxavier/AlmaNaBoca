package com.ixsvf.almanaboca.services.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.ixsvf.almanaboca.services.model.HomeItem
import kotlinx.coroutines.tasks.await

class HomeRepository {

    private val db = FirebaseFirestore.getInstance()
    // ID confirmado pelo seu print
    private val docRef = db.collection("home_items").document("nkC4lOyD6jZSxBw1k37E")

    suspend fun getHomeItems(): List<HomeItem> {
        return try {
            val snapshot = docRef.get().await()

            if (snapshot.exists()) {
                val listaFinal = mutableListOf<HomeItem>()

                // --- 1. LER CURSOS (Versão Universal) ---
                // Em vez de forçar List<Map>, pegamos o objeto genérico "Any"
                val rawData = snapshot.get("courses")

                // Verificamos se é uma lista (não importa do quê)
                if (rawData is List<*>) {
                    rawData.forEach { item ->
                        // Verificamos se cada item se comporta como um Mapa
                        if (item is Map<*, *>) {
                            try {
                                val homeItem = HomeItem(
                                    // Conversões Seguras (ToString para textos)
                                    coachProgram = item["coachProgram"]?.toString() ?: "",
                                    whatToExpectProgram = item["whatToExpectProgram"]?.toString() ?: "",
                                    coachStartDate = item["coachStartDate"]?.toString() ?: "",
                                    coachDateEnd = item["coachDateEnd"]?.toString() ?: "",
                                    idealFor = item["idealFor"]?.toString()?:"",
                                    theResult = item["theResult"]?.toString()?:"",
                                    youWillExperience = item["youWillExperience"]?.toString()?:"",
                                    contentAccess = item["contentAccess"]?.toString()?:"",
                                    coachType = item["coachType"]?.toString()?:"",

                                    // Conversões Seguras para Números
                                    coachDiscount = parseDouble(item["coachDiscount"]),
                                    coachVacancies = parseInt(item["coachVacancies"]),
                                    coachPriceOption1 = parseDouble(item["coachPriceOption1"]),
                                    coachPriceOption2 = parseDouble(item["coachPriceOption2"]),

                                    instagramUrl = item["instagramUrl"]?.toString() ?: ""
                                )

                                // Log para debug: Ver o que está a ser lido
                                Log.d("FIREBASE_DEBUG", "Li o programa: ${homeItem.coachProgram}")

                                if (homeItem.coachProgram.isNotEmpty()) {
                                    listaFinal.add(homeItem)
                                }
                            } catch (e: Exception) {
                                Log.e("FIREBASE_ERRO", "Falha ao ler item: ${e.message}")
                            }
                        }
                    }
                }

                // --- 2. LER MEDITAÇÃO ---
                val medType = snapshot.getString("meditationCirclesType") ?: ""
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
                }

                return listaFinal
            }
            Log.e("FIREBASE_ERRO", "Documento não existe ou ID errado")
            emptyList()
        } catch (e: Exception) {
            Log.e("FIREBASE_CRASH", "Erro fatal: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    // --- FUNÇÕES QUE EVITAM CRASHES DE TIPO ---
    private fun parseDouble(value: Any?): Double {
        if (value == null) return 0.0
        return when (value) {
            is Number -> value.toDouble() // Aceita Long, Int, Float, Double
            is String -> value.replace(",", ".").toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
    }

    private fun parseInt(value: Any?): Int {
        if (value == null) return 0
        return when (value) {
            is Number -> value.toInt()
            is String -> value.trim().toIntOrNull() ?: 0
            else -> 0
        }
    }
}