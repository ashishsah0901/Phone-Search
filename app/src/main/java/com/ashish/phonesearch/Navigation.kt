package com.ashish.phonesearch

import android.annotation.SuppressLint
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.SearchScreen.route) {
        composable(Screen.SearchScreen.route) {
            SearchScreen(navController = navController)
        }
        composable(
            Screen.ResultScreen.route + "/{search}",
            arguments = listOf(
                navArgument("search") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) {
            val searchKeyword = remember {
                it.arguments?.getString("search")
            }
            searchKeyword?.let {
                ResultScreen(searchText = searchKeyword)
            } ?: run {
                Text(text = "Please enter some text to search...")
            }
        }
    }
}