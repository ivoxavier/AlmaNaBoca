package com.ixsvf.almanaboca.services.repository.remote

import com.google.firebase.firestore.FirebaseFirestore

class FirebaseDataSource {
    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
}