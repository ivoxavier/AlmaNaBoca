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
    rootNavController: NavHostController // Este serve para ir para o Login ou Admin
) {
    // 1. CRIAR UM NOVO CONTROLADOR APENAS PARA AS ABAS (Resolve o Crash)
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = bottomNavController)
        }
    ) { innerPadding ->
        // Passamos os dois controladores
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
    rootNavController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = bottomNavController,
        startDestination = BottomBarScreen.Home.route,
        modifier = modifier
    ) {
        composable(route = BottomBarScreen.Meditations.route) {
            MeditationsScreen(modifier = Modifier.fillMaxSize())
        }

        composable(route = BottomBarScreen.Home.route) {
            HomeScreen(
                modifier = Modifier.fillMaxSize(),
                // IMPORTANTE: Passamos o 'rootNavController' para o HomeScreen
                // porque é ele que sabe ir para o ecrã de "admin_bookings"
                navController = rootNavController
            )
        }

        composable(route = BottomBarScreen.Booking.route) {
            BookingScreen(modifier = Modifier.fillMaxSize())
        }
    }
}