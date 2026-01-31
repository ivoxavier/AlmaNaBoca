package com.ixsvf.almanaboca.services.model

import com.google.firebase.firestore.PropertyName

data class HomeItem(
    val id: String = "",
    // Campos do Curso (Coaching)
    val coachName: String = "",
    val coachProgram: String = "",
    val whatToExpectProgram: String = "",
    val coachStartDate: String = "",
    val coachDateEnd: String = "",
    val coachPriceOption1: Double = 0.0,
    val option1WhatToExpect: String = "",
    val coachPriceOption2: Double = 0.0,
    val option2WhatToExpect: String = "",
    val coachDiscount: Double = 0.0,
    val coachVacancies: Int = 0,

    // Links
    val instagramUrl: String = "",
    val facebookUrl: String = "",
    val tiktokUrl: String = "",
    val linkedinUrl: String = "",
    val podcastUrl: String = "",
    val registrationsUrl: String = "",

    // Campos de Meditação (Lidos da Raiz)
    val meditationCirclesDesc: String = "",
    val meditationCirclesType: String = "",
    val meditationCirclesNextSession: String = "",
    val meditationCirclesLocation: String = "",
    val meditationCirclesPrice: Double = 0.0
)

// O Wrapper também usa Any? para não falhar na leitura da raiz
data class HomeDocumentWrapper(
    val courses: List<HomeItem> = emptyList(),
    val meditationCirclesType: String = "",
    val meditationCirclesDesc: String = "",
    val meditationCirclesNextSession: String = "",
    val meditationCirclesLocation: String = "",
    val meditationCirclesPrice: Any? = null
)