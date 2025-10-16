package com.example.myjetpack1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myjetpack1.model.CountryDataItem
import com.example.myjetpack1.ui.NavRoutes
import com.example.myjetpack1.ui.screens.CountryScreen
import com.example.myjetpack1.ui.screens.EnterAmountScreen
import com.example.myjetpack1.ui.screens.FiatListScreen
import com.example.myjetpack1.ui.screens.SettingScreen
import com.example.myjetpack1.ui.screens.UnitListScreen
import com.example.myjetpack1.ui.theme.MyJetpack1Theme
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyJetpack1Theme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = NavRoutes.COUNTRY_LIST) {
        composable(NavRoutes.COUNTRY_LIST) {
            CountryScreen(navController = navController)
        }
        composable(
            route = NavRoutes.SETTING_ROUTE,
            arguments = listOf(navArgument("countryDataJson") { type = NavType.StringType })
        ) { backStackEntry ->
            // Retrieve the JSON string from the arguments
            val json = backStackEntry.arguments?.getString("countryDataJson")

            // Decode the JSON string back to a CountryDataItem object
            val selectedCountryData = json?.let {
                Gson().fromJson(it, CountryDataItem::class.java)
            }

            //Pass the object to screen
            SettingScreen(
                navController = navController, // <-- Pass NavController
                selectedCountryData = selectedCountryData,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.UNIT_LIST) {
            UnitListScreen(navController = navController)
        }

        composable(NavRoutes.FIAT_LIST) {
            FiatListScreen(navController = navController)
        }

        composable(
            route = NavRoutes.ENTER_AMOUNT_ROUTE,
            arguments = listOf(
                navArgument("selectedUnit") { type = NavType.StringType },
                navArgument("selectedFiat") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val selectedUnit = backStackEntry.arguments?.getString("selectedUnit")
            val selectedFiat = backStackEntry.arguments?.getString("selectedFiat")

            EnterAmountScreen(
                navController = navController,
                selectedUnit = selectedUnit,
                selectedFiat = selectedFiat,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
