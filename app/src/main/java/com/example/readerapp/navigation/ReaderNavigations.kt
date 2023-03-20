package com.example.readerapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.readerapp.screens.SplashScreen
import com.example.readerapp.screens.authentication.CreateAccountScreen
import com.example.readerapp.screens.authentication.LoginScreen
import com.example.readerapp.screens.details.ReaderBookDetailsScreen
import com.example.readerapp.screens.home.HomeScreen
import com.example.readerapp.screens.search.SearchScreen
import com.example.readerapp.screens.stats.ReaderStatsScreen
import com.example.readerapp.screens.update.UpdateScreen

@Composable
fun ReaderNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ReaderScreens.SplashScreen.name) {
        composable(ReaderScreens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }
        
        composable(ReaderScreens.ReaderHomeScreen.name) {
            HomeScreen(navController = navController)
        }
        
        composable(ReaderScreens.CreateAccountScreen.name) {
            CreateAccountScreen(navController = navController)
        }
        
        composable(ReaderScreens.LoginScreen.name) {
            LoginScreen(navController = navController)
        }
        
        composable(ReaderScreens.DetailsScreen.name) {
            ReaderBookDetailsScreen(navController = navController)
        }
        
        composable(ReaderScreens.UpdateScreen.name) {
            UpdateScreen(navController = navController)
        }
        
        composable(ReaderScreens.ReaderStatsScreen.name) {
            ReaderStatsScreen(navController = navController)
        }
        
        composable(ReaderScreens.SearchScreen.name) {
            SearchScreen(navController = navController)
        }
    }
}