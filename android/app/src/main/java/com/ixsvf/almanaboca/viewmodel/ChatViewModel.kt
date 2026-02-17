package com.ixsvf.almanaboca.viewmodel

import androidx.compose.runtime.State // IMPORTANTE: Importar o State do Compose
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ixsvf.almanaboca.services.model.ChatMessage

class ChatViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    // Usamos o mutableStateOf do Compose para que a UI reaja automaticamente
    private val _messages = mutableStateOf<List<ChatMessage>>(emptyList())
    val messages: State<List<ChatMessage>> = _messages

    init {
        observeMessages()
    }

    private fun observeMessages() {
        db.collection("community_chat")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("CHAT_ERROR", "Erro ao ler mensagens: ${error.message}")
                    return@addSnapshotListener
                }

                snapshot?.let {
                    _messages.value = it.toObjects(ChatMessage::class.java)
                }
            }
    }

    fun sendMessage(text: String, userId: String, userName: String) {
        if (text.isBlank()) return

        // Criamos o objeto com o timestamp atual do sistema
        val msg = ChatMessage(
            senderId = userId,
            senderName = userName,
            text = text,
            timestamp = System.currentTimeMillis()
        )

        db.collection("community_chat")
            .add(msg)
            .addOnFailureListener { e ->
                android.util.Log.e("CHAT_ERROR", "Erro ao enviar: ${e.message}")
            }
    }
}