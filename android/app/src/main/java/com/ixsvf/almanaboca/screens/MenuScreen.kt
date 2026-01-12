package com.ixsvf.almanaboca.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import com.ixsvf.almanaboca.viewmodel.SessionViewModel


sealed class BottomBarScreen(
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val icon: Int){
    object Home : BottomBarScreen(AlmanaBocaConstants.NAVIGATION_ROUTES.MENU_SCREEN.HOME,
        R.string.lbl_btbar_home, R.drawable.ic_home)
    //object Meditations : BottomBarScreen(MMKConstants.NAVIGATION_ROUTES.MENU_SCREEN.MEDITATION,R.string.lbl_btbar_meditation, R.drawable.ic_meditation)
    //object Consultations : BottomBarScreen(MMKConstants.NAVIGATION_ROUTES.MENU_SCREEN.CONSULTATION,R.string.lbl_btbar_consultation, R.drawable.ic_consultation)
    //object Shop : BottomBarScreen(MMKConstants.NAVIGATION_ROUTES.MENU_SCREEN.SHOP,R.string.lbl_btbar_shop, R.drawable.ic_shop)
}

@Composable
fun MenuScreen(navController: NavController, sessionViewModel: SessionViewModel = viewModel())
{
    val bottomBarNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = bottomBarNavController) },

        )
    { paddingValues ->

        MenuNavHost(
            navController = bottomBarNavController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun MenuNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route,
        modifier = modifier // Aplicar modifier ao NavHost
    ) {
        composable(route = BottomBarScreen.Home.route) {
            // Passar o modifier para dentro da HomeScreen
            HomeScreen(modifier = modifier)
        }
        //composable(route = BottomBarScreen.Meditations.route) {
            // É boa prática passar o modifier para todos os ecrãs
            ////MeditationScreen()
        //}
        //composable(route = BottomBarScreen.Consultations.route) {
            //ConsultationScreen()
        //}
        //composable(route = BottomBarScreen.Shop.route) {
            //ShopScreen()
        //}
    }
}