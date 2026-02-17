package com.ixsvf.almanaboca.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ixsvf.almanaboca.services.model.ChatMessage

class ChatViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _messages = mutableStateOf<List<ChatMessage>>(emptyList())
    val messages: State<List<ChatMessage>> = _messages

    init {
        // Esta é a ÚNICA função que deve ler dados.
        // Se tiveres outras funções como 'observeMessages', APAGA-AS.
        db.collection("community_chat")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                if (snapshot != null) {
                    val chatList = snapshot.documents.mapNotNull { doc ->
                        val message = doc.toObject(ChatMessage::class.java)

                        // O SEGREDO ESTÁ AQUI:
                        // Estamos a criar uma cópia da mensagem forçando o ID do documento.
                        // Se isto não for feito, o id fica "" e a app crasha.
                        message?.copy(id = doc.id)
                    }
                    _messages.value = chatList
                }
            }
    }

    fun sendMessage(text: String, userId: String, userName: String) {
        if (text.isBlank()) return

        val msg = ChatMessage(
            senderId = userId,
            senderName = userName,
            text = text,
            timestamp = System.currentTimeMillis()
        )

        db.collection("community_chat").add(msg)
    }
}