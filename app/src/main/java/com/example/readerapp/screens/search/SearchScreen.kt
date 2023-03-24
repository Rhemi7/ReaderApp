package com.example.readerapp.screens.search

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.readerapp.components.InputField
import com.example.readerapp.components.ReaderAppBar
import com.example.readerapp.model.Item
import com.example.readerapp.navigation.ReaderScreens

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Preview
@Composable
fun SearchScreen(navController: NavController = NavController(LocalContext.current), viewModel: SearchViewModel = hiltViewModel()) {

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
        Surface() {


            Column {
                SearchForm(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                ) {query ->
                    viewModel.searchBooks(query)

                }
                Spacer(modifier = Modifier.height(13.dp))
                BookList(navController, viewModel)
            }
        }
    }
}

@Composable
fun BookList(navController: NavController, viewModel: SearchViewModel) {
    val listOfBooks = viewModel.list

    if (viewModel.isLoading) {
       LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(listOfBooks) { book ->
                BookRowCard(book = book, navController = navController)
            }
        }
    }



}

@Composable
fun BookRowCard(book: Item, navController: NavController) {
    Card(modifier = Modifier
        .padding(3.dp)
        .fillMaxWidth()
        .height(100.dp)
        .clickable {
            navController.navigate(ReaderScreens.DetailsScreen.name + "/${book.id}")
        },
        elevation = 7.dp
    ) {
        Row(verticalAlignment = Alignment.Top) {
            val imageUrl = if(book.volumeInfo.imageLinks.smallThumbnail.isEmpty())
                "https://images.unsplash.com/photo-1541963463532-d68292c34b19?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=80&q=80"
            else {
                book.volumeInfo.imageLinks.smallThumbnail
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
                Text(text = book.volumeInfo.title.toString(), overflow = TextOverflow.Ellipsis)
                Text(
                    text = "Authors: ${book.volumeInfo.authors}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.caption
                )

                Text(
                    text = "Date: ${book.volumeInfo.publishedDate}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.caption
                )

                Text(
                    text = "${book.volumeInfo.categories}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.caption
                )

            }
        }

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchForm(modifier: Modifier = Modifier,
//               viewModel: SearchViewModel,
               loading: Boolean = false,
               hint: String = "Search",
               onSearch: (String) -> Unit = {}) {
    Column {
        val searchQueryState = rememberSaveable{
            mutableStateOf("")
        }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(searchQueryState.value) {
            searchQueryState.value.trim().isNotEmpty()
        }
        
        InputField(
            valueState = searchQueryState,
            labelId = "Search",
            enabled = true,
            onAction = KeyboardActions {
                if (!valid) return@KeyboardActions
                onSearch(searchQueryState.value.trim())
                searchQueryState.value = ""
                keyboardController?.hide()
            })
    }
}