package com.example.readerapp.screens.details

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.readerapp.components.ReaderAppBar
import com.example.readerapp.data.Resource
import com.example.readerapp.model.Item
import com.example.readerapp.navigation.ReaderScreens

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ReaderBookDetailsScreen(navController: NavController,
                            bookId: String,
                            viewModel: DetailsViewModel = hiltViewModel()) {

    Scaffold(topBar = {
        ReaderAppBar(title = "Book Details", icon = Icons.Default.ArrowBack , showProfile = false, navController = navController) {
            navController.navigate(ReaderScreens.SearchScreen.name)
        }
    }) {
        Surface(modifier = Modifier
            .padding(3.dp)
            .fillMaxSize()) {
            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val bookInfo = produceState<Resource<Item>>(initialValue = Resource.Loading()) {
                    value = viewModel.getBookInfo(bookId)
                }.value

                if (bookInfo.data == null) {
                    LinearProgressIndicator()
                } else {
                    Text("Book Id: ${bookInfo.data?.volumeInfo?.title}")


                }
            }
        }
    }

}