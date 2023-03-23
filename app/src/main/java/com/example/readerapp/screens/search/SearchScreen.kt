package com.example.readerapp.screens.search

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.readerapp.components.ReaderAppBar
import com.example.readerapp.navigation.ReaderScreens

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Preview
@Composable
fun SearchScreen(navController: NavController = NavController(LocalContext.current)) {

    Scaffold(topBar = {
        ReaderAppBar(
            title = "Search Books",
            icon = Icons.Default.ArrowBack,
            navController = navController,
            showProfile = false,
        ) {
            navController.navigate(ReaderScreens.ReaderHomeScreen.name)
        }
    }) {

    }
}