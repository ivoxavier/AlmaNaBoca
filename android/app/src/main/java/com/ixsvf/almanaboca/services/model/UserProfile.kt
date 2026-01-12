package com.ixsvf.almanaboca.services.model

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val isActive : Boolean
)