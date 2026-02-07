package com.ixsvf.almanaboca.services.model

import com.google.firebase.Timestamp

data class Booking(
    val id: String = "", // ID do documento do Firebase
    val date: String = "", // "2026-02-15"
    val time: String = "", // "14:00"
    val serviceType: String = "",
    val clientName: String = "",
    val clientEmail: String = "",
    val clientPhone: String = "",
    val status: String = "confirmed",
    val createdAt: Timestamp? = null
)