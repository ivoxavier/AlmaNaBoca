package com.ixsvf.almanaboca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ixsvf.almanaboca.screens.LoginScreen
import com.ixsvf.almanaboca.screens.MenuScreen
import com.ixsvf.almanaboca.screens.menusubscreens.CommunityChatScreen
import com.ixsvf.almanaboca.screens.menusubscreens.UserScreen
import com.ixsvf.almanaboca.screens.menusubscreens.YouTubeScreen
import com.ixsvf.almanaboca.ui.theme.AlmaNaBocaTheme
import com.ixsvf.almanaboca.ui.theme.states.LoginUiState
import com.ixsvf.almanaboca.viewmodel.SessionViewModel
// Importe o AdminBookingScreen se necessário
// import com.ixsvf.almanaboca.screens.menusubscreens.AdminBookingScreen

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
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
    sessionViewModel: SessionViewModel = viewModel()
) {
    val navController = rememberNavController()
    val startDestination = Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // --- LOGIN ---
        composable(route = Screen.Login.route) {
            val uiState by sessionViewModel.uiState.collectAsState()

            LaunchedEffect(uiState) {
                if (uiState is LoginUiState.Success) {
                    sessionViewModel.updateFcmToken(sessionViewModel.currentUser.value?.email ?: "")

                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }

            LoginScreen(
                uiState = uiState,
                onLoginClick = { email, password ->
                    sessionViewModel.login(email, password)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // --- MENU PRINCIPAL ---
        composable(route = Screen.Menu.route) {
            // CORREÇÃO: Passamos o navController com o nome 'rootNavController'
            MenuScreen(rootNavController = navController)
        }

        composable(
            route = "player/{videoId}",
            arguments = listOf(navArgument("videoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString("videoId") ?: ""
            // Chama o ecrã que criámos no Passo 2
            //com.ixsvf.almanaboca.screens.menusubscreens.YouTubeScreen(videoId = videoId)
            YouTubeScreen(videoId = videoId)
        }

        composable("user_screen") {
            UserScreen(navController = navController, sessionViewModel = sessionViewModel)
        }

        composable("community_chat") {
            CommunityChatScreen(navController = navController)
        }
    }
}