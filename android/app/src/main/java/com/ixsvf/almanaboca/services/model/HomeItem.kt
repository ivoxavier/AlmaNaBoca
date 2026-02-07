package com.ixsvf.almanaboca.services.model

data class HomeItem(
    val id: String = "",
    // Campos do Curso
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

    // Campos de Meditação
    val meditationCirclesDesc: String = "",
    val meditationCirclesType: String = "",
    val meditationCirclesNextSession: String = "",
    val meditationCirclesLocation: String = "",
    val meditationCirclesPrice: Double = 0.0
)