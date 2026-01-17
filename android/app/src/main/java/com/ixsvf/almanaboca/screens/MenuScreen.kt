package com.ixsvf.almanaboca.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ixsvf.almanaboca.R
import com.ixsvf.almanaboca.constants.AlmanaBocaConstants
import com.ixsvf.almanaboca.screens.components.BottomNavigationBar
import com.ixsvf.almanaboca.screens.menusubscreens.HomeScreen
import com.ixsvf.almanaboca.viewmodel.SessionViewModel


sealed class BottomBarScreen(
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val icon: Int
) {
    object Meditations : BottomBarScreen(
        AlmanaBocaConstants.NAVIGATION_ROUTES.MENU_SCREEN.MEDITATION,
        R.string.lbl_btbar_meditation,
        R.drawable.ic_meditation
    )

    object Booking : BottomBarScreen(
        AlmanaBocaConstants.NAVIGATION_ROUTES.MENU_SCREEN.BOOKING,
        title = R.string.lbl_btbar_booking,
        icon = R.drawable.ic_consultation
    )
    // object Meditations : BottomBarScreen(...)
    // object Consultations : BottomBarScreen(...)
    // object Shop : BottomBarScreen(...)
}

@Composable
fun MenuScreen(
    navController: NavController,
    sessionViewModel: SessionViewModel = viewModel()
) {
    // This controller manages ONLY the bottom bar tabs (Home, Shop, etc.)
    val bottomBarNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = bottomBarNavController) }
    ) { paddingValues ->
        // We pass the paddingValues to the Host via modifier
        MenuNavHost(
            navController = bottomBarNavController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun MenuNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Meditations.route,
        modifier = modifier // 1. The NavHost applies the Scaffold padding here
    ) {
        composable(route = BottomBarScreen.Meditations.route) {
            // 2. Do NOT pass 'modifier' here. The NavHost already handled the padding.
            // Just let HomeScreen fill the available space inside the NavHost.
            HomeScreen(modifier = Modifier.fillMaxSize())
        }

        composable(route = BottomBarScreen.Booking.route) {
            // 2. Do NOT pass 'modifier' here. The NavHost already handled the padding.
            // Just let HomeScreen fill the available space inside the NavHost.
            HomeScreen(modifier = Modifier.fillMaxSize())
        }
    }
}