package com.ixsvf.almanaboca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import com.ixsvf.almanaboca.screens.LoginScreen
import com.ixsvf.almanaboca.screens.MenuScreen
import com.ixsvf.almanaboca.ui.theme.AlmaNaBocaTheme

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
fun AlmanaBocaNavigation()
{
    val context = LocalContext.current


    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(route = Screen.Menu.route) {
            MenuScreen(navController = navController)
        }
    }
}



