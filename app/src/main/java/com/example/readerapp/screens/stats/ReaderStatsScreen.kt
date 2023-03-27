package com.example.readerapp.screens.stats

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.sharp.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.readerapp.components.ReaderAppBar
import com.example.readerapp.model.Item
import com.example.readerapp.model.MBook
import com.example.readerapp.navigation.ReaderScreens
import com.example.readerapp.screens.home.HomeScreenViewModel
import com.example.readerapp.screens.search.BookRowCard
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
            
            Column {
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
                        Text(text = "You've read: ${readBooksList.size} books")

                    }
                }

                if (viewModel.dataR.value.loading == true) {
                    LinearProgressIndicator()
                } else {
                    Divider()
                    LazyColumn(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(), contentPadding = PaddingValues(16.dp)) {

                        val readBooks: kotlin.collections.List<MBook> = if (!viewModel.dataR.value.data.isNullOrEmpty()) {
                            viewModel.dataR.value.data!!.filter { mBook ->
                                (mBook.userId == currentUser?.uid) && (mBook.finishedReading != null)
                            }
                        } else {
                            emptyList()
                        }

                        items(items = readBooks) {
                            book ->
                            BookStatsRowCard(book =book)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookStatsRowCard(book: MBook) {
    Card(modifier = Modifier
        .padding(3.dp)
        .fillMaxWidth()
        .height(100.dp)
        .clickable {
        },
        elevation = 7.dp
    ) {
        Row(verticalAlignment = Alignment.Top) {
            val imageUrl = if(book.photoUrl?.isEmpty() == true)
                "https://images.unsplash.com/photo-1541963463532-d68292c34b19?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=80&q=80"
            else {
                book.photoUrl
            }
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Book Image",
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .padding(
                        end = 4.dp
                    )
            )
            Column() {
                Text(text = book.title.toString(), overflow = TextOverflow.Ellipsis)
                Text(
                    text = "Authors: ${book.authors}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.caption
                )

                Text(
                    text = "Date: ${book.publishedDate}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.caption
                )

                Text(
                    text = "${book.categories}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.caption
                )

            }
        }

    }
}