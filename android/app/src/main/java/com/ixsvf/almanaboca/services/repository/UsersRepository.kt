package com.ixsvf.almanaboca.services.repository

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class UsersRepository(application: Application) {

    // Instância do FirebaseAuth
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Tenta fazer login com email e password.
     * @return O objeto FirebaseUser se sucesso.
     * @throws Exception Se o login falhar (ex: password errada, sem rede).
     */
    suspend fun signInWithEmailAndPassword(email: String, pass: String): FirebaseUser? {
        return try {
            // .await() suspende a coroutine até o Firebase responder
            val authResult = auth.signInWithEmailAndPassword(email, pass).await()

            // Retorna o utilizador logado
            authResult.user
        } catch (e: Exception) {
            // Relança a exceção para que o ViewModel a capture e mostre o erro na UI
            throw e
        }
    }
}