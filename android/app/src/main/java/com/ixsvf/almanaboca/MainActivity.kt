package com.ixsvf.almanaboca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ixsvf.almanaboca.screens.LoginScreen
import com.ixsvf.almanaboca.screens.MenuScreen
import com.ixsvf.almanaboca.ui.theme.AlmaNaBocaTheme
import com.ixsvf.almanaboca.viewmodel.SessionViewModel

// Definição simples das rotas para este ficheiro (ou mova para um ficheiro separado)
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Menu : Screen("menu")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlmaNaBocaTheme {
                AlmanaBocaNavigation()
            }
        }
    }
}

@Composable
fun AlmanaBocaNavigation(
    // Podemos injetar o ViewModel aqui para partilhar estado se necessário
    sessionViewModel: SessionViewModel = viewModel()
) {
    // 1. AQUI ESTÁ A CORREÇÃO: Criar o navController
    val navController = rememberNavController()

    // 2. Definir o destino inicial
    val startDestination = Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Rota de Login
        composable(route = Screen.Login.route) {
            // Ligar o LoginScreen ao ViewModel e à Navegação
            val uiState by sessionViewModel.uiState.collectAsState()

            LoginScreen(
                uiState = uiState,
                onLoginClick = { email, password ->
                    sessionViewModel.login(email, password)
                    // Num cenário real, observaria o estado "Success" para navegar
                    // Por enquanto, forçamos a navegação para teste:
                    navController.navigate(Screen.Menu.route) {
                        // Remove o Login da pilha para não voltar atrás com o botão "Back"
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Rota do Menu Principal
        composable(route = Screen.Menu.route) {
            MenuScreen(navController = navController)
        }
    }
}