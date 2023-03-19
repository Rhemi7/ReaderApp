package com.example.readerapp.navigation

enum class ReaderScreens {
    SplashScreen,
    LoginScreen,
    CreateAccountScreen,
    ReaderHomeScreen,
    SearchScreen,
    DetailsScreen,
    UpdateScreen,
    ReaderStatsScreen;


    companion object {
        fun fromRoute(route: String?): ReaderScreens
        = when(route?.substringBefore("/")) {
            SplashScreen.name -> SplashScreen
            LoginScreen.name -> LoginScreen
            CreateAccountScreen.name -> CreateAccountScreen
            ReaderStatsScreen.name -> ReaderStatsScreen
            SearchScreen.name -> SearchScreen
            DetailsScreen.name -> DetailsScreen
            UpdateScreen.name -> UpdateScreen
            ReaderHomeScreen.name -> ReaderHomeScreen
            null -> ReaderHomeScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}