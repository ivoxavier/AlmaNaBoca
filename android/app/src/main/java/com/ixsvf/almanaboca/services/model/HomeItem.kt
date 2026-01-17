package com.ixsvf.almanaboca.services.model

import kotlinx.serialization.Serializable

@Serializable
data class HomeItem(
    val id : String = "",
    val coachName : String = "",
    val coachProgram : String = "",
    val whatToExpectProgram : String = "",
    val mentorName : String = "",
    val mentorHistory : String = "",
    val mentorTags : String = "",
    val coachDateStart : String = "",
    val coachDateEnd : String = "",
    val coachPriceOption1 : Double = 0.00,
    val option1WhatToExpect : String = "",
    val coachPriceOption2 : Double = 0.00,
    val option2WhatToExpect : String = "",
    val coachDiscount : Double = 0.00,
    val coachVacancies : Int = 0,
    val instagramUrl : String = "",
    val facebookUrl : String = "",
    val tiktokUrl : String = "",
    val linkedinUrl : String = "",
    val podcastUrl : String = "",
    val registrationsUrl : String = ""
)