package com.ixsvf.almanaboca.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ixsvf.almanaboca.R
import com.ixsvf.almanaboca.screens.components.BottomNavigationBar
import com.ixsvf.almanaboca.screens.menusubscreens.BookingScreen
import com.ixsvf.almanaboca.screens.menusubscreens.HomeScreen
import com.ixsvf.almanaboca.screens.menusubscreens.MeditationsScreen

// Definição dos itens da Barra
sealed class BottomBarScreen(
    val route: String,
    val title: Int,
    val icon: ImageVector
) {
    object Meditations : BottomBarScreen("meditations", R.string.lbl_btbar_meditation, Icons.Default.SelfImprovement)
    object Home : BottomBarScreen("home", R.string.lbl_btbar_home, Icons.Default.Home)
    object Booking : BottomBarScreen("booking", R.string.lbl_btbar_booking, Icons.Default.BookOnline)
}

@Composable
fun MenuScreen(
    rootNavController: NavHostController
) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = bottomNavController)
        }
    ) { innerPadding ->
        MenuNavHost(
            bottomNavController = bottomNavController,
            rootNavController = rootNavController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun MenuNavHost(
    bottomNavController: NavHostController,
    rootNavController: NavHostController, // Usamos este para o player (para tapar a barra de baixo)
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = bottomNavController,
        startDestination = BottomBarScreen.Home.route,
        modifier = modifier
    ) {
        // 1. TELA DE MEDITAÇÕES
        composable(route = BottomBarScreen.Meditations.route) {
            MeditationsScreen(
                modifier = Modifier.fillMaxSize(),
                onVideoClick = { videoId ->
                    // --- AQUI ESTAVA A FALTAR A NAVEGAÇÃO ---
                    // Navegamos usando o rootNavController para sair das abas e abrir o player
                    rootNavController.navigate("player/$videoId")
                }
            )
        }

        // 2. TELA HOME
        composable(route = BottomBarScreen.Home.route) {
            HomeScreen(
                modifier = Modifier.fillMaxSize(),
                navController = rootNavController
            )
        }

        // 3. TELA BOOKING
        composable(route = BottomBarScreen.Booking.route) {
            BookingScreen(modifier = Modifier.fillMaxSize())
        }

        // 4. NOVA ROTA: PLAYER DE VÍDEO (Adicione isto se ainda não tiver no root graph,
        // mas como estamos dentro do MenuNavHost que usa o bottomNavController,
        // o ideal é definir esta rota no AlmanaBocaNavigation (MainActivity).
        // Vê a explicação abaixo.
    }
}