package com.example.readerapp.screens.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.readerapp.components.*
import com.example.readerapp.model.MBook
import com.example.readerapp.navigation.ReaderScreens
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController, homeScreenViewModel: HomeScreenViewModel = hiltViewModel()) {
    Scaffold(topBar = {
                      ReaderAppBar(title = "A.Reader", navController = navController)
    },
        floatingActionButton = {
        FabContent(onTap = {
            navController.navigate(ReaderScreens.SearchScreen.name)
        })
        }) {
            Surface(modifier = Modifier.fillMaxSize()) {
                HomeContent(navController, homeScreenViewModel)
            }
    }
}



@Composable
fun ReadingRightNowArea(books: List<MBook>, navController: NavController) {
    HorizontalScrollableComponent(books) {
        Log.d("TAG", "BookListArea: $it")
    }
//    BookListCard()
}

@Composable
fun HomeContent(navController: NavController, homeScreenViewModel: HomeScreenViewModel = hiltViewModel()) {

    var listOfBooks = emptyList<MBook>()
    val currentUser = FirebaseAuth.getInstance().currentUser

    if(!homeScreenViewModel.dataR.value.data.isNullOrEmpty()) {
        if (currentUser != null) {
            listOfBooks= homeScreenViewModel.dataR.value.data?.toList()!!.filter { mBook ->
                mBook.userId == currentUser.uid.toString()
            }

            Log.d("User Books", "HomeContent: ${listOfBooks.toString()}")
        }
    }

//    val listOfBooks = listOf<MBook>(
//        MBook(id = "gddh", title = "Things fall apart", authors = "All of us", notes = null),
//        MBook(id = "gddh", title = "Things fall apart", authors = "All of us", notes = null),
//        MBook(id = "gddh", title = "Things fall apart", authors = "All of us", notes = null),
//        MBook(id = "gddh", title = "Things fall apart", authors = "All of us", notes = null),
//        MBook(id = "gddh", title = "Things fall apart", authors = "All of us", notes = null)
//    )

    val email = FirebaseAuth.getInstance().currentUser?.email
    val currentUserName = if (!email.isNullOrEmpty()) email.split("@")[0] else "N/A"
    Column(modifier = Modifier.padding(2.dp), verticalArrangement = Arrangement.Top) {
        Row(modifier = Modifier.align(alignment = Alignment.Start)) {
            TitleSection(label = "Your reading \n activity right now")
            Spacer(modifier = Modifier.fillMaxWidth(0.7f))
            Column {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .clickable {
                            navController.navigate(ReaderScreens.ReaderStatsScreen.name)
                        }
                        .size(45.dp), tint = MaterialTheme.colors.secondaryVariant
                )
                Text(
                    text = currentUserName,
                    modifier = Modifier
                        .padding(2.dp)
                        .align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.overline,
                    color = Color.Red,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip)

                Divider()
            }
        }
        ReadingRightNowArea(books = listOfBooks, navController = navController)

        TitleSection(label = "Reading List")

        BookListArea(listOfBooks = listOfBooks, navController = navController)
    }
}

@Composable
fun BookListArea(listOfBooks: List<MBook>, navController: NavController) {
    HorizontalScrollableComponent(listOfBooks) {
        navController.navigate(ReaderScreens.UpdateScreen.name + "/$it")
    }
}

@Composable
fun HorizontalScrollableComponent(listOfBooks: List<MBook>, onCardPressed : (String) -> Unit) {
    val scrollState = rememberScrollState()
    
    Row(modifier = Modifier
        .fillMaxWidth()
        .heightIn(280.dp)
        .horizontalScroll(state = scrollState)) {

        for (book in listOfBooks) {
            BookListCard(book) {
                onCardPressed(book.googleBookId.toString())
            }
        }
    }
}










