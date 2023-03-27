package com.example.readerapp.screens.stats

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.sharp.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.readerapp.components.ReaderAppBar
import com.example.readerapp.model.MBook
import com.example.readerapp.screens.home.HomeScreenViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ReaderStatsScreen(navController: NavController, viewModel: HomeScreenViewModel = hiltViewModel()) {
    var books: List<MBook>
    val currentUser = FirebaseAuth.getInstance().currentUser

    Scaffold(topBar = {
        ReaderAppBar(
            title = "Book Stats",
            icon = Icons.Default.ArrowBack,
            showProfile = false,
            navController = navController
        ) {
            navController.popBackStack()
        }
    }) {
        Surface() {
            books = if (!viewModel.dataR.value.data.isNullOrEmpty()) {
                viewModel.dataR.value.data!!.filter {mBook ->
                    (mBook.userId == currentUser?.uid)

                }
            } else {
                emptyList()
            }
            
            Column() {
                Row() {
                    Box(modifier = Modifier
                        .size(45.dp)
                        .padding(2.dp)) {
                        Icon(imageVector = Icons.Sharp.Person, contentDescription = "Icon")
                    }
                    Text(text = "Hi, ${
                        currentUser?.email.toString().split("@")[0].uppercase(
                            Locale.ROOT
                        )
                    }")
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    shape = CircleShape,
                    elevation = 5.dp
                ) {
                    val readBooksList: List<MBook> = if (!viewModel.dataR.value.data.isNullOrEmpty()) {
                        books.filter { mBook: MBook ->
                            (mBook.userId == currentUser?.uid) && (mBook.finishedReading != null)
                        }
                    } else {
                        emptyList()
                    }

                    val readingBooks = books.filter {
                        mBook ->
                        (mBook.startedReading != null && mBook.finishedReading == null)
                    }

                    Column(
                        modifier = Modifier.padding(start = 25.dp, top = 4.dp, bottom = 4.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = "Your Stats", style = MaterialTheme.typography.h5)
                        Divider()
                        Text(text = "You're reading: ${readingBooks.size} books")
                        Text(text = "You're reading: ${readBooksList.size} books")

                    }
                }
            }
        }
    }
}